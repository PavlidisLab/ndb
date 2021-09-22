import os
import operator
import petl
import utils
from marvmodel import AbstractModel
from genemap import annovar_to_ncbi

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
                         'CADD13_raw',
                         'CADD13_phred',
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
        self.variant_ordered = []
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
        self.variant_ordered = []
        header = variants[0]

        VARIANT_ID_IDX = header.index("id")
        CHROMOSOME_IDX = header.index("chromosome")
        START_IDX = header.index("start_hg19")
        REF_IDX = header.index("ref")
        ALT_IDX = header.index("alt")

        contents = []
        for row in variants[1:] :
            self.variant_ordered.append(row[VARIANT_ID_IDX])
            content = list(operator.itemgetter(CHROMOSOME_IDX, START_IDX, REF_IDX, ALT_IDX)(row))
            contents.append( content )

        # Create VCF
        template = []
        templated = []
        with open("flows/res/template.vcf", "r") as f:
            # Extract rows from vcf templates
            for line in f:
                template.append(line.strip())

        # The last line in the template has a variant format template
        variant_row = template[-1]

        # Rest of file is the header
        template = template[:-1]

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

            # Use the variant ID as a sample ID to allow duplicates in vcf
            line = line.replace("%ID", str(self.variant_ordered[VARIANT_INDEX]))

            templated.append(line)

            #key = ":".join( [str(x) for x in [CHR_vcf, POS_vcf, REF_vcf, ALT_vcf]] )
            key = VARIANT_INDEX

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
                        print "Key error; Record too short."
                        print record
                        raw_input()
                        continue

                    # Match input annotated against record from annovar.
                    if key in annotated_dict.keys():
                        if record != annotated_dict[key]:
                            print "Key is in record, but is not the same"
                            print annotated_dict[key]
                            print "vs"
                            print record
                            raise Exception("Key is in record, but is not the same: " + str(annotated_dict[key])  + " vs " + str(record) )                                
                    annotated_dict[key] = record


        self.variant_ordered = [] # Empty and repopulate since records might be missing from annovar

        for v_id in annotated_dict.keys():
            #print "ID:", v_id,
            #raw_input()

            try:
                a = annotated_dict[v_id]
            except:                
                raise Exception("Variant ID '" + str(v_id) + "'  not found in annotated_dict")

            annos = a[-1]
            anno_dict = {}
            AAChange = "."
            Function = []
            annotations = annos.split(";")

            self.variant_ordered.append(v_id)
            func_text = None
            aa_text = None
            gene_text = None

            for element in annotations:
                
                if element == "." or "=" not in element:
                    continue
                try:
                    k,v = element.split("=")
                except:
                    print "================> WARNING: Skipping", element
                    raise Exception("Annotation not found in value " + str(v) + " for key " + str(k) )

                if v == ".":
                    v = None

                #------------------#
                if k == "GERP++_RS":
                    k = "GERP_RS"
                if k == "ExAC_ALL":
                    k = "exac03"

                if  k == 'CADD13_RawScore':
                    k = 'CADD13_raw'
                if k == 'CADD13_PHRED':
                    k = 'CADD13_phred'

                if k == "AAChange.refGene":
                    # TODO: Added fix but not sure if correct.
                    if v is None:
                        aa_text = ""
                    else:
                        aa_text = v.replace("\\x3b", ";") # Hex encoding issue with annovar. 
                        aa_text = aa_text.replace("x3b", ";") # Hex encoding issue with annovar.
                        aa_text = ";".join( list(set(aa_text.split(";"))) )
                    k = "aa_change"
                    v = aa_text

                if k == "Func.refGene":
                    if v is not None:
                        func_text = v.replace("\\x3b", ";") # Hex encoding issue with annovar.
                        func_text = func_text.replace("x3b", ";") # Hex encoding issue with annovar.
                        func_text = ";".join( list(set(func_text.split(";"))) )
                    else:
                        func_text = ""

                    k = "func"
                    v = func_text

                if k == "Gene.refGene":
                    gene_text = v

                anno_dict[k] = v
                # Print key values
                # print k, v
                
            ########################

            # Load update vectors
            self.aa_change.append(aa_text)
            self.function.append(func_text)
            self.gene.append(gene_text)

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
        ACCEPT_FUTURE_NONE = False

        # UPDATE PER VARIANTS
        # Check that vectors are the same length
        sanity_check = [len(x) for x in [self.variant_ordered, self.aa_change, self.function, self.gene, self.category]]
        if (sum(sanity_check) / float(len(sanity_check))) != sanity_check[0] :
            raise Exception("Sanity check failed; vectors are not the same length.")

        for i in xrange(len( self.variant_ordered )) :
            print "INDEX",i
            row = self.data[i]
            variant_id = self.variant_ordered[i] #row[0]
            where = " id = '"+str(variant_id)+"' "
            
            # print row
            # print "variant_id, self.gene[i], self.aa_change[i], self.function[i], self.category[i]"
            # print variant_id, self.gene[i], self.aa_change[i], self.function[i], self.category[i]
            # raw_input()
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

            if self.category[i]:
                self.U.update_table_rows_by_field("variant",
                                                  "category",
                                                  self.category[i],
                                                  where)


            ####### Update variant_gene
            if self.gene[i]:
                gene = self.gene[i]
                synonyms = None
                if gene == "NONE,NONE":
                    print "[WARNING] Annovar receives NONE,NONE, as the gene here."
                    print "Please check this is not an error and fix properly."
                    gene = "NONE"
                    
                    block_on_answer = True
                    while block_on_answer and not ACCEPT_FUTURE_NONE:
                        print "[C]ontinue | [A]bort | [F]orce 'Continue' for all further cases in the paper"
                        accept_none = raw_input()
                        if accept_none in ["C", "A", "F"]:
                            block_on_answer = False                        

                        if accept_none == "F":
                            print "Accepting all NONEs for this paper"
                            ACCEPT_FUTURE_NONE=True
                        elif accept_none == "A":
                            print "Abort."
                            raise Exception("Annovar returned NONE,NONE as the gene.")
                        elif accept_none == "C":
                            pass ## Nothing to do, block on answer already off.

                while True :
                    answer = ""

                    if gene is None:
                        answer = "continue"
                        break

                    query = "SELECT gene_id FROM gene WHERE symbol='"+gene+"';"
                    result = self.U.fetch_rows(query)


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
                        # The returned record format is gene_id, symbol, synonyms (pipe delimited)
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
                        
                        # Check each synonyms for a match
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

                        if gene in annovar_to_ncbi.keys():
                            """
                            Handle cases where the annovar gene does not work with the NCBI gene table
                            """
                            replacement = annovar_to_ncbi[gene]
                            gene = replacement
                            continue
                        
                        print "Exception raised while fetching gene:", gene
                        print result
                        print query
                        print "Enter 'quit' to abort everything. Not that some updates might have been effected."
                        print "Enter 'continue' to skip gene."
                        print "Enter a delimiter (e.g. ',') to split genes and use the first one"
                        print "Enter GENE=... to force the gene name."


                        # Trouble shootingspecific cases

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
                            # Abort
                            raise e
                        elif answer == "continue":
                            # Don't give it a gene
                            g_id = None
                            break
                        elif answer[:5] == "GENE=":
                            # Try again with user inputed gene
                            gene=answer[5:]
                            print "Trying with user input:", gene
                        else:
                            # Use a delimiter
                            print "Splitting using", answer, "as delimiter."
                            gene = gene.split(answer)[0]


                # This is the only wait the variant_gene step gets skipped.
                if answer == "continue":
                    answer = ""
                    continue # FIXME: I think this should be break, not continue ...

                if g_id is not None:
                    variant_gene = [ ["gene_id", "variant_id"], [str(g_id), str(variant_id)] ]
                    r = petl.appenddb( variant_gene,
                                       self.U.connection,
                                       "variant_gene")

            else:
                print "Variant", variant_id, "has no annovar genes."
                print "Continue? [Y]/n"
                r = raw_input()
                if r == 'n': sys.exit(0)

            ##### This should actually be done in annovar because we need the variant_id
            g_header = ["variant_id", "gene_id"]

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
