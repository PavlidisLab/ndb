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
            # Extract rows from vcf templates
            for line in f:
                template.append(line.strip())

        variant_row = template[-1]
        template = template[:-1]

        ordered = [] # Used later since annovar doesn't handle duplicates well.
        VARIANT_INDEX = -1
        for content in contents:
            VARIANT_INDEX += 1
            CHR_vcf, POS_vcf, REF_vcf, ALT_vcf = str(content[0]), str(content[1]), str(content[2]), str(content[3])

            line = variant_row[:]
            line = line.strip()
            line = line.replace("%CHROMOSOME", CHR_vcf)
            line = line.replace("%POSITION", POS_vcf )

            if not REF_vcf:
                REF_vcf = "."
            if not ALT_vcf:
                ALT_vcf = "."

            line = line.replace("%REF", REF_vcf)
            line = line.replace("%ALT", ALT_vcf)
            line = line.replace("%ID", str(variant_ids[VARIANT_INDEX]))


            templated.append(line)

            #key = ":".join( [str(x) for x in [CHR_vcf, POS_vcf, REF_vcf, ALT_vcf]] )
            key = VARIANT_INDEX
            ordered.append(key)

        # TODO: It would be nice if these files had names specific to the input.
        TMP_DIR = "flows/tmp/"
        VCF_FILE = TMP_DIR + "todo.vcf"
        if(not os.path.isdir(TMP_DIR)):
            os.makedirs(TMP_DIR)
        filesToRemove = [f for f in os.listdir(TMP_DIR)]
        [os.remove(TMP_DIR + f) for f in filesToRemove]

        with open(VCF_FILE, "w") as f:
            for line in ( template + templated ):
                f.write( line + "\n" )

        os.system("./remote_annovar.sh ")


        ANNOTATED_FILE = VCF_FILE + ".hg19_multianno.vcf"
        annotated = []
        annotated_dict = {}
        with open(ANNOTATED_FILE, 'r') as f:
            for line in f:
                if line[0] == "#":
                    continue
                else:
                    record = line.strip()
                    record = record.split("\t")

                    try:
                        #key = ":".join( [str(x) for x in [record[0], record[2], record[3], record[4]] ] )
                        key = record[2]

                    except:
                        print "WEIRD RECORD!"
                        print record
                        raw_input()
                        continue
                    if key in annotated_dict.keys():
                        print key, "exists!"
                        if record != annotated_dict[key]:
                            print "AND IS NOT THE SAME!"
                            print annotated_dict[key]
                            print "vs"
                            print record
                            raw_input()
                        else : print "But is the same."

                    annotated_dict[key] = record
                    annotated.append(record)

        annotations = {}
        for v_id, o in zip(variant_ids, ordered):
            print "ID:", v_id, o
            #a = annotated_dict[o]
            try:
                a = annotated_dict[str(v_id)]
            except:
                print annotated_dict.keys()
                exit()
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
                if k == "ExAC_ALL":
                    k = "exac03"
                if k == "AAChange.refGene":
                    # TODO: Added fix but not sure if correct.
                    aa_text = v.replace("\\x3b", ";") # Hex encoding issue with annovar. 
                    aa_text = func_text.replace("x3b", ";") # Hex encoding issue with annovar.
                    aa_text = ";".join( list(set(func_text.split(";"))) )
                    self.aa_change.append(v)
                    k = "aa_change"
                if k == "Func.refGene":
                    func_text = v.replace("\\x3b", ";") # Hex encoding issue with annovar.
                    func_text = func_text.replace("x3b", ";") # Hex encoding issue with annovar.
                    func_text = ";".join( list(set(func_text.split(";"))) )
                    self.function.append(func_text)
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
                category_text = category_text.replace("\\x3b", ";") # Hex encoding issue with annovar.
                category_text = category_text.replace("x3b", ";") # Hex encoding issue with annovar.
                category_text = ";".join( list(set(category_text.split(";"))) )
                self.category.append(category_text)
            else:
                self.category.append(None)

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
                    raw_input()
                    print " Escaping the error for now."
                    continue

                while True:
                    query = "SELECT gene_id FROM gene WHERE symbol='"+gene+"';"
                    result = self.U.fetch_rows(query)
                    answer = ""
                    try:
                        g_id =  int(result[1][0])
                        break
                    except Exception as e:

                        """ ATTEMPT TO RESOLVE BY REMOVING READTHROUGH """
                        if "-" and "," in gene:
                            genes = gene.split(",")
                            splitgenes = []
                            for splitgene in genes:
                                if "-" in splitgene:
                                    continue
                                else:
                                    splitgenes.append(splitgene)
                            
                            if len(splitgenes) == 1:
                                gene = splitgenes[0]
                                print "Resolved by removing 'readthrough' genes; using", gene
                                #raw_input()
                                continue
                                
                        
                        """ ATTEMPT TO RESOLVE BY SYNONYMS """
                        query_synonyms = "SELECT gene_id, symbol, synonyms FROM gene WHERE synonyms LIKE '%"+gene+"%';"
                        synonyms = self.U.fetch_rows(query_synonyms)
                        print "---- Potential synonyms for ", gene, "----"
                        for row in synonyms:
                            print row
                        print "----------------------------"

                        if len(synonyms) == 2 and gene in synonyms[1][2].split("|"):
                            gene = synonyms[1][1] # Set name to the symbol where gene was a synonym.
                            print "Resolved by synonyms (type 1). Using gene as synonym."
                            #raw_input()
                            continue
                        
                        if len(synonyms) > 2:
                            replacement = None
                            for synonym in synonyms[1:] :
                                if gene in synonym[2].split("|"):
                                    if replacement is not None:
                                        print "ERROR! Multiple match for", replacement, "when parsing", synonym
                                    else:
                                        replacement = synonym[1] # Set name to the symbol where gene was a synonym.
                                        print "Resolved by synonyms (type 2). Using gene as synonym."
                            gene = replacement
                            continue
                        
                        print "Exception raised while fetching gene:", gene
                        print result
                        print query
                        print "Enter 'quit' to abort everything. Not that some updates might have been effected."
                        print "Enter 'continue' to skip gene."
                        print "Enter a delimiter (e.g. ',') to split genes and use the first one"
                        print "Enter GENE=... to force the gene name."

                        if "," in gene:
                            # If comma is in gene, split and use the first one.
                            answer = ","
                        elif "\\x" in gene:
                            # Sometimes, "\x3b" or \x2c get inserted for delimiters.
                            answer = "\\x"
                        elif gene[:3] == "LOC" or gene == "NONE":
                            # To make this automated and more straight forward, skip any LOC as they wont be in geneinfo.
                            # Important that this condition set checks for , or \x before anything else. At this point we assume we have no other options.
                            answer = "continue"
                        else:
                            # If not, prompt user for judicious opinion.
                            answer = raw_input()

                        if answer == "quit":
                            raise e
                        elif answer == "continue":
                            break
                        elif answer[:5] == "GENE=":
                            gene=answer[5:]
                            print "SETTING GENE NAME TO:", gene
                        else:
                            gene = gene.split(answer)[0]


                if answer == "continue":
                    answer = ""
                    continue
                answer = ""

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
