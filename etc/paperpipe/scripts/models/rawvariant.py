import petl
import utils
from marvmodel import AbstractModel
from rawkv import RawKV

class RawVariant(AbstractModel):
    __properties_list = "paper_id,raw_id,sample_id,chromosome,start_hg19,stop_hg19,ref,alt,gene,effect,code_change,protein_change,aa_change,variant,strand,denovo,lof".split(",")
    INSTRUCTION_SET = [
        "_assigned",
        "_autoincrement",
        "_stamp",
        "_linked",
        "_strand"
    ]

    def __init__(self, paper_id, **kwargs):
        super(RawVariant, self).__init__()
        self.paper_id = paper_id
        #self.book = book
        self.raw_ids = []

        self.sheet = "rawvariant"
        self.database_table = self.DATABASE_TABLES["raw_variant"]
        self.rkv_table = self.DATABASE_TABLES["raw_key_value"]

        self.properties_list =  RawVariant._RawVariant__properties_list
        self.rkv = None
        self.refGene = "../res/refGene.txt"


    def set_rkv(self, rkv):
        self.rkv = rkv

    def load_instructions(self, filename, sheet=None, range_string=None, row_offset=0, column_offset=0, **kwargs):
        #return petl.io.xlsx.fromxlsx(filename, sheet, range_string, row_offset=0, column_offset=0, **kwargs)
        return self.load_sheet(filename, sheet=sheet, range_string=range_string, row_offset=row_offset, column_offset=column_offset, **kwargs)

    def load(self, filename, **kwargs):
        to_commit = []

        rkv_list = self.U.fetch_table_rows_by_paper(self.rkv_table, self.paper_id)
        rkv_header = rkv_list[0]
        rkv_list = list(rkv_list[1:])

        PAPER_IDX = rkv_header.index('paper_id')
        RAW_IDX = rkv_header.index('raw_id')
        KEY_IDX = rkv_header.index('key')
        VALUE_IDX = rkv_header.index('value')

        # LOAD RKV in buckets by RAW ID
        # Each bucket contains properties for the given variant
        rkv_buckets = {}

        for rkv in rkv_list:
            #print rkv
            # Dictionary of raw_id -> property1,property2,property3 etc...
            raw_id = rkv[RAW_IDX]
            if raw_id not in rkv_buckets.keys():
                rkv_buckets[raw_id] = [ ]
            rkv_buckets[ raw_id ].append( [rkv[KEY_IDX], 
                                           rkv[VALUE_IDX]] )
        ##################################################################

        # LOAD INSTRUCTIONS
        instructions = self.load_instructions(filename, sheet=self.sheet, **kwargs)
        instructions_header = [x for x in instructions[:1]][0]
        instructions = instructions[1:]

        FIELD_IDX = instructions_header.index(u'Field')
        FIELDMAPPING_IDX = instructions_header.index(u"Mapping")
        MAPPINGVALUE_IDX = instructions_header.index(u"Value")

        valid_instructions = []
        for ins in instructions:
            # Filter instructions with empty fields
            if ins[FIELD_IDX] is not None:
                valid_instructions.append(ins)
        instructions = valid_instructions                

        strands = {}

        ##################################################################
        # TRANSFORM DATA ACCORDING TO INSTRUCTIONS
        #for k in rkv_buckets.keys() :
        #    print k,":",rkv_buckets[k]        
        
        for raw_id in sorted( rkv_buckets.keys() ):
            """
            For each Raw ID, go through instructions and fetch important properties
            """
            #print "RAW_ID", raw_id
            bucket = rkv_buckets[raw_id]
            ducket = {}
            for k in bucket:
                ducket[k[0]] = k[1]
            """
            for k in ducket.keys():
                print k,ducket[k]
            """

            header = ["paper_id", "raw_id"]
            variant = [ self.paper_id, raw_id ]
            
            for ins in instructions:
                command = ins[FIELD_IDX]

                print "command:", command
                if command in RawVariant.INSTRUCTION_SET:        
                    """ Handle instructions by type """
                    if command == '_stamp':
                        # Apply the MAPPINGVALUE value as data, wrap in list
                        col_name = [ ins[FIELDMAPPING_IDX]  ]
                        data = [ ins[MAPPINGVALUE_IDX]  ]                      
                    elif command == '_linked':
                        # Fetch MAPPINGVALUE as the property to fetch
                        
                        # Not sure if we need to do something here...
                        # The only cases that are _linked are paper_id and raw_id 
                        # which is handled by the code logic, so _linked might
                        # not be necessary...
                        # We could also implement this as the paper_id and raw_id 
                        continue
                    elif command == '_strand':
                        # TODO: Handle other ways than by transcript ID
                        # Adjust the strand of the transcript based of RefGene.

                        if len(strands.keys()) < 1:                       
                            TRANSCRIPT_IDX = 1
                            STRAND_IDX = 3
                            with open( self.refGene , 'r') as f:
                                for _refGene in f: # Header included by that's ok
                                    refGene = _refGene.strip().split("\t")
                                    #print refGene[TRANSCRIPT_IDX], "=", refGene[STRAND_IDX]
                                    strands[refGene[TRANSCRIPT_IDX]] = refGene[STRAND_IDX]

                        else:
                            pass
                        
                        k = ins[FIELDMAPPING_IDX] 
                        v = ins[MAPPINGVALUE_IDX] 
                        
                        transcript_field, v = v.split(",")
                        
                        try:
                            transcript = ducket[transcript_field]
                            col_name = [ k ]                            
                            allele = ducket[v]
                        except:
                            print transcript_field, v
                            print "Well it was this one"
                            
                        try:
                            #print "IT WASN'T BECAUSE OF", transcript, v, transcript_field
                            #print strands.keys()
                            #print "strand is in transcript?"
                            #print (transcript in strands.keys())
                            #raw_input()
                            #print "STRAND:", strands[transcript]
                            #print "DATA:", data
                            data = ""
                            if strands[transcript] == "-":
                                for token in allele[:]:
                                    if token == 'A':
                                        data += 'T'
                                    elif token == 'C':
                                        data += 'G'
                                    elif token == 'G':
                                        data += 'C'
                                    elif token == 'T':
                                        data += 'A'
                            else:
                                data = allele
                            data = [ data ]      

                        except:
                            print "Error before reverse completements."
                            raise RuntimeError("Error using " + type(x).__name__ + " " + str(x) +
                                               " for command'" +command+
                                               " Transcript: " + transcript+
                                               "'. Check that input file is correctly mapped." )

                        

                    else:
                        raise RuntimeError("List of known instructions in " + type(x).__name__ +" includes '"+command+"' but no code was made to catch it." )                        
                else:
                    """ Direct mapping for RawKV """                
                    k = ins[FIELDMAPPING_IDX] 
                    v = ins[FIELD_IDX] 

                    try:
                        col_name = [ k ]
                        data = [ ducket[v] ]                        
                    except:
                        raise RuntimeError("Raw key-value in " + type(x).__name__ + 
                                           " does not include key'"+command+
                                           "'. Check that input file is correctly mapped." )
                    
                    
                header += col_name
                variant +=  data 
            to_commit.append(variant)
                
        # Prepare data for commiting
        self.data = [header] + to_commit
        #self.commit()

    #@Override
    def commit(self):
        print "\n".join([str(x) for x in self.data])
        petl.appenddb(self.data, self.U.connection, self.database_table)
        return self.data
        
    def usage(self):
        print """
        a = RawVariant()
        
        a.load("../../exampledata/dyrk1a/variants.xlsx")
        
        a.commit()
        """

def bootstrap():
    # TODO: Remove for production
    raw_variants = RawVariant(7)
    raw_variants.load("../../exampledata/dyrk1a/variants.xlsx")
    return raw_variants


if __name__ == "__main__":
    bootstrap()
