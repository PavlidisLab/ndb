import os
import operator
import petl
import utils
from marvmodel import AbstractModel


class Annovar(AbstractModel):
    __properties_list = ['variant_id',
                         'SIFT_score',
                         'SIFT_pred',
                         'Polyphen2_HDIV_score',
                         'Polyphen2_HDIV_pred',
                         'Polyphen2_HVAR_score',
                         'Polyphen2_HVAR_pred',
                         'LRT_score',
                         'LRT_pred',
                         'MutationTaster_score',
                         'MutationTaster_pred',
                         'MutationAssessor_score',
                         'MutationAssessor_pred',
                         'FATHMM_score',
                         'FATHMM_pred',
                         'RadialSVM_score',
                         'RadialSVM_pred',
                         'LR_score',
                         'LR_pred',
                         'VEST3_score',
                         'CADD_raw',
                         'CADD_phred',
                         'GERP_RS',
                         'phyloP46way_placental',
                         'phyloP100way_vertebrate',
                         'SiPhy_29way_logOdds',
                         'exac03',
                         'clinvar_20150629'
    ]

    def __init__(self, paper_id, **kwargs):
        super(Annovar, self).__init__()
        self.paper_id = paper_id
        self.sheet = "annovar"
        self.database_table= "annovar_scores"
        self.properties_list = Annovar._Annovar__properties_list
        self.data = []
        self.function = []
        self.aa_change = [] 
        self.gene = []
        self.category = []

    def insert(self, datadict):
        row = [datadict[k] for k in self.properties_list]
        self.data.append(row)

    def load(self, filename):
        #TODO: You could get requirement's table name instead 
        variants = self.U.fetch_table_rows_by_paper("variant" , self.paper_id)
        variant_ids = []
        header = variants[0]
        VARIANT_ID_IDX = header.index("id")
        CHROMOSOME_IDX = header.index("chromosome")
        START_IDX = header.index("start_hg19")
        REF_IDX = header.index("ref")
        ALT_IDX = header.index("alt")
        
        contents = []
        for row in variants[1:] :        
            variant_ids.append(row[VARIANT_ID_IDX])
            content = list(operator.itemgetter(CHROMOSOME_IDX, START_IDX, REF_IDX, ALT_IDX)(row))
            contents.append( content )

        # Create VCF
        template = []
        templated = []
        with open("flows/res/template.vcf", "r") as f:
            for line in f:
                template.append(line.strip())
            variant_row = template[-1]
            template = template[:-1]
            #print "\n".join(template)
            for content in contents:
                line = variant_row[:]
                line = line.strip()
                line = line.replace("%CHROMOSOME", content[0])
                line = line.replace("%POSITION", str(content[1]) )
                if content[2]:
                    line = line.replace("%REF", content[2])
                else:
                    line = line.replace("%REF", ".")
                if content[3]:
                    line = line.replace("%ALT", content[3])
                else:
                    line = line.replace("%ALT", ".")
                templated.append(line)


        TMP_DIR = "flows/tmp/"
        VCF_FILE = TMP_DIR + "todo.vcf"
        
        filesToRemove = [f for f in os.listdir(TMP_DIR)]
        [os.remove(TMP_DIR + f) for f in filesToRemove]

        with open(VCF_FILE, "w") as f:
            for line in ( template + templated ):
                f.write( line + "\n" )

        os.system("./remote_annovar.sh ")
        
        ANNOTATED_FILE = VCF_FILE + ".hg19_multianno.vcf"
        annotated = []
        with open(ANNOTATED_FILE, 'r') as f:
            for line in f:
                if line[0] == "#":
                    continue
                else:
                    record = line.strip()
                    record = record.split("\t")
                    annotated.append(record)

        annotations = {}

        for v_id, a in zip(variant_ids, annotated):
            print "ID:", v_id
            annos = a[-1]
            anno_dict = {}
            AAChange = "."
            Function = []
            annotations = annos.split(";")
            for element in annotations:

                if element == ".":
                    continue
                try:
                    k,v = element.split("=")
                except:
                    print "================> WARNING: Skipping", element

                if v == ".":
                    v = None

                #------------------#
                if k == "GERP++_RS":
                    k = "GERP_RS"
                if k == "AAChange.refGene":
                    self.aa_change.append(v)
                    k = "aa_change"
                if k == "Func.refGene":
                    self.function.append(v)
                    k = "func"
                if k == "Gene.refGene":
                    self.gene.append(v)

                anno_dict[k] = v
                print k, v            

            if anno_dict["func"] ==  "splicing" and \
               anno_dict["ExonicFunc.refGene"] == None:
                anno_dict["category"] = "splicing"
                self.category.append("splicing")
            elif "ExonicFunc.refGene" in anno_dict.keys() and anno_dict["ExonicFunc.refGene"]:
                category_text = anno_dict["ExonicFunc.refGene"].replace("_", " ")
                self.category.append(category_text)
            #else: it's intergenic
            
            anno_dict["variant_id"] = v_id
            self.insert(anno_dict)
        

    def commit(self):
        """
        Push model's to append to the database
        """
        tbl = [self.properties_list] + self.data
        print "Insert:"
        for t in tbl:
            print t
        print "Update:"
        print self.aa_change
        print self.function

        # UPDATE PER VARIANTS
        for i in range(len( self.data )) :
            print "INDEX",i
            row = self.data[i]
            variant_id = row[0]
            where = " id = '"+str(variant_id)+"' "

            if self.aa_change[i]:
                self.U.update_table_rows_by_field("variant", 
                                                  "aa_change",
                                                  self.aa_change[i],
                                                  where)
            if self.function[i]:
                self.U.update_table_rows_by_field("variant", 
                                                  "func",
                                                  self.function[i],
                                                  where) 
            if len(self.category) > i and self.category[i]:
                self.U.update_table_rows_by_field("variant", 
                                                  "category",
                                                  self.category[i],
                                                  where) 


            ####### Update variant_gene
            if self.gene[i]:
                gene = self.gene[i]
                if gene == "NONE,NONE":
                    print "WARNING! WARNING! WARNING!"
                    print "Annovar receives NONE,NONE, as the gene here. Possibly because the variant is on the X chromsome."
                    print "Please check this error and fix properly"
                    print " Escaping the error for now."
                    continue
                query = "SELECT gene_id FROM gene WHERE symbol='"+gene+"';"
                result = self.U.fetch_rows(query)
                
                try:
                    g_id =  int(result[1][0])
                except Exception as e:
                    print result
                    print query
                    print gene
                    raise e
                    

                variant_gene = [ ["gene_id", "variant_id"], [str(g_id), str(variant_id)] ]
                r = petl.appenddb( variant_gene, 
                                   self.U.connection, 
                                   "variant_gene")                                                         
            else:
                print "Variant", variant_id, "has no annovar genes."
                print "Continue? Y/n"
                r = raw_input()
                if r == 'n': sys.exit(0)
                
            
            ##### This should actually be done in annovar because we need the variant_id
            g_header = ["variant_id", "gene_id"]
            #g_data = [ row[  ]
                                       
        # Update the annovar table
        r = petl.appenddb( tbl, self.U.connection, self.database_table )

        return tbl
    
    def delete(self):
        """
        Requires custom delete method since it doesn't have a paper_id
        """

        # Clear annovar data from variant table
        for _data in self.data:
            variant_id = _data[0]
            self.U.delete_table_rows_by_field(self.database_table, variant_id, "variant_id")

            # Clear categoryfrom Variant table
            ## Not required
            
            # Clear variant_gene table link
            self.U.delete_table_rows_by_field("variant_gene", variant_id, "variant_id")        

        return self.data

    def usage(self):
        print """
        Write usage
        """
