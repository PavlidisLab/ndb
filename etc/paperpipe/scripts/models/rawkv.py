import petl
import utils
from marvmodel import AbstractModel

class RawKV(AbstractModel):
    __properties_list = ['paper_id',
                         'raw_id',
                         'key',
                         'value']
    
    def __init__(self, paper_id, **kwargs):
        super(RawKV, self).__init__()
        self.paper_id = paper_id
        self.sheet = "rawdata"
        self.database_table= self.DATABASE_TABLES["raw_key_value"]
        self.properties_list =  RawKV._RawKV__properties_list

    def load(self, filename, **kwargs):
        print "Loading paper."
        rkv = list(self.U.load_paper(filename, sheet=self.sheet, **kwargs))
        header = None
        
        COUNTER = 0
        print "Obtaining header."
        for row in rkv:
            header = row
            break
        
        header = [str(x) for x in header]

        rows = []
        print "Processing headers."
        #for r in xrange(1, len(rkv)):
        for r,tmp in enumerate(rkv):
            if r == 0: continue
            print "Header r=",r,"/",len(rkv)
            #row = [str(x) if type(x) == int else x for x in rkv[r]]
            row = [str(x) if type(x) == int else x for x in tmp]
            paperid_vector = [self.paper_id] * len(row)
            rawid_vector = [r] * len(row)

            #print rkv
            #response=raw_input()
            #if response == "-1": exit(-1)
            rows.append( zip(paperid_vector, rawid_vector, header, row ) )


        # TODO: Remove; likely dead code
        table = []
        MAXROWS = len(rows)
        for r in rows:
            print "Row numbers:", COUNTER, "/", MAXROWS
            COUNTER+=1
            table.extend( r )

        self.data = table
        # self.data = rows
        self.data = [self.properties_list] + self.data


    def commit(self):
        """
        Override the commit method for raw key values
        """
        try:
            if len(self.data) < 1000:
                petl.appenddb(self.data, self.U.connection, self.database_table)
            else:

                self.U.resetDB()                
                
                head = self.data[0]
                rest = self.data[1:]
                BINSIZE = 1000
                
                for chunk in self.U.chunks(rest, BINSIZE):
                    dataSlice = [head] + chunk
                    petl.appenddb(dataSlice, self.U.connection, self.database_table)                    
                    
                    
        except Exception as e:
            print "EXCEPTION on insert to " , self.database_table
            print self.data[0]
            print self.data[1]
            print "..."
            print self.data[-1]
            raise e
        return self.data
        
    def usage(self):
        print """
        a = RawKV()
        
        a.load("../../exampledata/dyrk1a/variants.xlsx")
        
        a.commit()
        """
