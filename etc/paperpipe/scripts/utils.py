import petl
import pymysql

import hgvs.dataproviders.uta
import hgvs.parser
from hgvs.assemblymapper import AssemblyMapper

from math import floor

class Utils(object):
    def __init__(self):
        
        self.initDB()
        self.assembly="GRCh37"
        self.hdp = hgvs.dataproviders.uta.connect()
        self.parser = hgvs.parser.Parser()
        self.vm = AssemblyMapper(
            self.hdp,
            assembly_name=self.assembly,
            alt_aln_method='splign')


        pass

    def initDB(self):
        dbconfigs = {}
        with open("db.config", 'r') as f:
            for line in f:
                if line[0] == "#": continue
                k,v = (line.strip()).split("=")
                dbconfigs[k] = v

        self.dbuser=dbconfigs['dbuser']
        self.dbpass=dbconfigs['dbpass']
        self.db=dbconfigs['db']
        self.host=dbconfigs['host']
        if self.host == "": self.host="localhost"

        self.connection = pymysql.connect(host=self.host,
                                          user=self.dbuser, 
                                          password=self.dbpass, 
                                          database=self.db,
                                          charset='utf8')
        self.connection.cursor().execute('SET SQL_MODE=ANSI_QUOTES')

    def resetDB(self):
        self.connection.close()
        self.initDB()

    def chunks(self, l, n):
        """Yield successive n-sized chunks from l."""
        for i in xrange(0, len(l), n):
            yield l[i:i + n]

    def transform(self, i, f, o, paper_id=None):
        """
         i : input
         f : function
         o : output
        """
        
        h = o.split(",")
        d = None

        if f in ['parse_hgvs', 'parse_reformat_hgvs']:
            
            d = None
            if f == 'parse_reformat_hgvs':
                d = self.convert_hgvs(i, REFORMAT=True)
            else:
                d = self.convert_hgvs(i)
            
            if d['uncertain'] == True:
                raise RuntimeError("HGVS mapping reportedly 'uncertain' : '"+d['uncertain']+"'")
            if d == None:
                d = [ None ] * 5
            else:
                d = [d['chr'],
                     d['start'],
                     d['end'],
                     d['ref'],
                     d['alt'],
                 ]
        elif f == 'prepend_paper':
            d = [str("Paper#"+str(paper_id)+":"+str(i))]

        elif f == 'connect':
            if i == 'NULL': 
                d = [None]
            else: 
                d = [int(i)]
        else:
            raise RuntimeError("Unknown instruction: '" +f+ "'")

        return h, d
        

    def intersection(self, raw, real, paper_id):
        """
        Return column names that intersect between raw and real
        """
        l1 = self.fetch_table_rows_by_paper(raw, paper_id)[0]
        l2 = self.fetch_table_rows_by_paper(real, paper_id)[0]
        li = [l1.index(x) for x in l1 if x in l2]
        return li

    def load_paper(self, filename, sheet=None, range_string=None, row_offset=0, column_offset=0, **kwargs):
        return petl.io.xlsx.fromxlsx(filename, sheet, range_string, row_offset=0, column_offset=0, **kwargs)


    def fetch_table_rows_by_paper(self, table_name, paper_id):
        rows = petl.fromdb(self.connection, 
                           ' SELECT * FROM ' + table_name +
                           " WHERE paper_id='" +str(paper_id)+ "' ; ")
        return rows

    def delete_table_rows_by_paper(self, table_name, paper_id):
        statement = ' DELETE FROM ' + table_name + " WHERE paper_id='" +str(paper_id)+ "' ; "
        print "******************"
        print statement
        print "******************"
        rows = self.execdb(statement)
        self.connection.commit()

        return rows

    def delete_table_rows_by_id(self, table_name, id):
        statement = ' DELETE FROM ' + table_name + " WHERE id='" +str(id)+ "' ; "
        print "******************"
        print statement
        print "******************"
        rows = self.execdb(statement)
        self.connection.commit()

        return rows

    def update_table_rows_by_field(self, table_name, field, value, where):
        statement = 'UPDATE ' + table_name + " SET "+field+"='" +str(value)+ "' WHERE "+where+" ; "
        print "******************"
        print statement
        print "******************"
        rows = self.execdb(statement)
        self.connection.commit()

        return rows


    def delete_table_rows_by_field(self, table_name, id, field):
        statement = ' DELETE FROM ' + table_name + " WHERE "+field+"='" +str(id)+ "' ; "
        print "******************"
        print statement
        print "******************"
        rows = self.execdb(statement)
        self.connection.commit()

        return rows


    def execdb(self, statement):            
        return self.connection.cursor().execute( statement )
        
    def fetch_rows(self, statement):
        return petl.fromdb(self.connection, statement)

    def parse_hgvs(self, _var_c):
        """
        Returns chromosome:start-stop coordinates from a given HGVS variant string
        """
        MAX_ATTEMPS = 25 # Maximum number of attemps to infer the transcript version
        var_c = _var_c
        var_g = None
        check_me = True
        index = 1
        r = None
        while not var_g and check_me:
            try:
                var_g = self.vm.c_to_g(
                    self.parser.parse_hgvs_variant(var_c)
                )
                check_me = False
            except Exception as e:
                if index > MAX_ATTEMPS:
                    print "Could not increment to a proper version, please check this transcript and that it exists in UTA"
                    print "Ignoring", var_c
                    r = 9
                if not r:
                    print "Failed to match hgvs using", var_c
                    print e
                    print e.message
                    
                    print "Possible solutions:"
                    print "1 - Add/Increment versions of transcript?"
                    print "2 - Decrement versions of transcript?"
                    print "9 - Ignore variant."
                    print "0 - Abort?"
                    
                    r = 1
                    #raw_input()

                if str(r) == "0":
                    print r, ": abort."
                    exit(0)
                elif str(r) == "1":
                    if index == 1 and var_c.count(".") > 1:
                        # Case where we already have a version number but it's not in UTA
                        raise NotImplementedError
                    else:
                        # Case where version is straight-up missing.
                        # Rewrite variant:
                        
                    # also, simply increment
                        var_c = _var_c.replace(":", "."+str(index)+":")
                        index += 1
                        print "Retrying with", var_c, "."                        
                elif str(r) == "9":
                    # Ignoring variant
                    return None
                else:
                    print "Unknown option:", r
                    raise NotImplementedError                                        

        return var_g

    def NC_to_chr(self, NC):
        return str(int(floor(float( 
            NC.replace("NC_", "").strip("0") 
        ))))

    def explode_hgvs(self, g):
        data = {}

        if g is None:
            data['chr'] = None
            data['start'] = None
            data['end'] = None
            data['ref'] = "N"
            data['alt'] = "N"
            data['uncertain'] = None
            return data

        data['chr'] = self.NC_to_chr(g.ac)
        data['start'] = g.posedit.pos.start.base
        data['end'] = g.posedit.pos.end.base
        data['ref'] = g.posedit.edit.ref
        data['alt'] = g.posedit.edit.alt
        data['uncertain'] = g.posedit.uncertain

        return data

    def convert_hgvs(self, _c, REFORMAT=False):
        if REFORMAT and ">" not in _c:
            """
            Attempt at fixing non-standard code changes
            E.g: NM_001199378:c.G3194A
            Should become NM_001199378:c.3194G>A
            """
            transcript, c = _c.split(".")
            queue = []
            buff = ""
            for x in c:
                # STEP 1: Collect non number characters after c.
                if ord(x) in xrange(ord('A'), ord('Z')):
                    buff += x
                elif len(buff) > 0:
                    queue.append(buff)
                    buff = ""
            if len(buff) > 0:
                queue.append(buff) # In case the buffer is still filled.
                buff = "" 

            if len(queue) > 2:
                print "ERROR PARSING", _c
                print "Queue:", queue
                raise ValueError("Queue has more than two blocks of nucleotide changes!")
                exit()
            
            # STEP 2: Remove block of nucleotides (now stored in queue)
            for x in queue:
                c = c.replace(x, "")
            
            # STEP 3: Add two blocks of nucleotide after coordinate, joined with >
            c += ">".join(queue)
            c = transcript + "." + c
        else:
            c = _c
        return self.explode_hgvs( self.parse_hgvs(c) )

    def test(self):
        """ Test"""
    
        hgvs_c = [
            'NM_001396.3:c.1098+1G>A',
            'NM_001396.3:c.1240-2A>G',
            'NM_001396.3:c.516+2T>C',
            'NM_001396.3:c.665-9_665-5del',
            'NM_001396.3:c.208-1G>A']

        header = ['chr', 'start','end','ref','alt','uncertain']

        for c in hgvs_c:
            x = self.convert_hgvs(c)
            print [x[y] for y in header]
