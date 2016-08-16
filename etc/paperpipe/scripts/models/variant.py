import petl
import utils
from marvmodel import AbstractModel
from rawvariant import RawVariant

class Variant(AbstractModel):
    __properties_list = "id,paper_id,raw_variant_id,event_id,subject_id,sample_id,chromosome,start_hg18,start_hg19,stop_hg19,ref,alt,gene,category,code_change,protein_change,good_mutation,gene_detail,func,aa_change,cytoband,denovo,lof".split(",")

    __excluded = "id".split(",")

    def __init__(self, paper_id, **kwargs):
        super(Variant, self).__init__()
        self.paper_id = paper_id

        self.sheet = "variant"
        self.database_table = self.DATABASE_TABLES['variant']
        
        self.properties_list = Variant._Variant__properties_list
        self.excluded = Variant._Variant__excluded

    def load(self, filename, **kwargs):
        to_commit = []

        # LOAD input-function-output (ifo) mapping
        ifo = self.load_sheet(filename, sheet=self.sheet, **kwargs)
        ifo_header = [x for x in ifo[:1]][0]
        ifo = list(ifo[1:])

        INPUT_IDX = ifo_header.index(u'input')
        FUNCTION_IDX = ifo_header.index(u"function")
        OUTPUT_IDX = ifo_header.index(u"output")

        raw_variants = self.U.fetch_table_rows_by_paper( RawVariant(self.paper_id).database_table, 
                                                         self.paper_id )
        raw_header = raw_variants[0]
        commit_header = []
        commit_data = []

        intersection =  self.U.intersection( RawVariant(self.paper_id).database_table, 
                                             self.database_table, 
                                             self.paper_id )


        for r in raw_variants[1:]:
            variant = {}

            # Transfer unmodified values            
            for i in intersection:
                variant[raw_header[i]] = r[i]
            
            # Apply modifications
            for _transform in ifo:
                #transform = [z.strip() if z is not None else None for z in _transform]
                transform = [str(z).strip() for z in _transform if z is not None]
                #print _transform
                #print transform

                transform_input = None
                try:
                    "Attempt to get data from raw variant"
                    if transform[0] == 'NULL':
                        IDX=0
                    else:
                        IDX = raw_header.index( transform[0] )
                        transform_input = r[IDX]
                except:
                    "Use value directly instead of looking up raw variant"
                    transform_input = transform[0]

                h, d = self.U.transform( transform_input, transform[1], transform[2], paper_id = self.paper_id )               
                for h,d in zip(h,d):
                    variant[h] = d
                
            ks = variant.keys()
            vs = []
            for k in ks:
                vs.append(variant[k])                
            
            # Remove excluded fields or NoneTypes
            pops = []
            for i in range(len(vs)):
                if vs[i] is None or \
                   ks[i] in self.excluded:
                    pops.append(i)
            ks = [ks[x] for x in range(len(ks)) if x not in pops]
            vs = [vs[x] for x in range(len(vs)) if x not in pops]

            if self.data is None:
                self.data = []
            self.data.append([ ks, vs])
        ###########################################

    def precommit(self, _data):
        #print "DATA:",_data
        data = []
        FIELD = 'subject_id'

        for i in range(len(_data)):
            header, row = _data[i]
            IDX = header.index(FIELD)
            #x = row[:]
            
            START = row[header.index('start_hg19')]
            STOP = row[header.index('stop_hg19')]
        
            existingID = self.disambiguate_subjects(START, 
                                                    STOP, 
                                                    tolerance=0)
            if existingID:
                row[IDX] = existingID
            
            if str(row[IDX]) == "-1":                
                row[IDX] = self.get_biggest_ID(FIELD, table=self.database_table)
            data.append([header, row])        

        return data
        
    def commit(self):
        for _data in self.data:
            data = self.precommit([_data])[0]
            petl.appenddb(data, self.U.connection, self.database_table, commit=True)
            #print data[0]
            #print data[1]
            #print "Commit"
        return self.data
        
    def disambiguate_subjects(self, start, stop, tolerance=0):

        for RANGE in range(0, tolerance + 1):
            RANGE_START_left = str( start - RANGE ) 
            RANGE_START_right = str( start + RANGE ) 

            RANGE_STOP_left = str( stop - RANGE ) 
            RANGE_STOP_right = str( stop + RANGE ) 


            query = "SELECT * FROM {0} WHERE start_hg19 >= {1} AND start_hg19 <= {2} AND stop_hg19 >= {3}  AND stop_hg19 <= {4} ;".format(self.database_table, 
                                                                                                                                          RANGE_START_left, 
                                                                                                                                          RANGE_START_right, 
                                                                                                                                          RANGE_STOP_left, 
                                                                                                                                          RANGE_STOP_right)         
            #print query
            _rows = self.U.fetch_rows(query)
            idxs = []
            if _rows:
                header = _rows[0]
                SID = header.index("subject_id")
                rows = _rows[1:]
                
                for r in rows:
                    idxs.append( r[SID] )
                
            if len(idxs) < 1:
                continue
            else:
                for x in idxs:
                    if x != idxs[0]:
                        raise Exception("Multiple subject IDs for one position!")
                    else:
                        pass # Good
                return idxs[0] # Found a disambiguation at this level

        return None

def bootstrap():
    # TODO: Remove for production
    variants = Variant(0)
    variants.load("../../exampledata/dyrk1a/variants.xlsx")
    variants.commit()
    return variants


if __name__ == "__main__":
    bootstrap()
