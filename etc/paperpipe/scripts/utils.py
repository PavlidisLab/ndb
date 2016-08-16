import petl
import pymysql

import hgvs.dataproviders.uta
import hgvs.parser
import hgvs.variantmapper

from math import floor

class Utils(object):
    def __init__(self):
        
        dbconfigs = {}
        with open("db.config", 'r') as f:
            for line in f:
                k,v = (line.strip()).split("=")
                dbconfigs[k] = v

        self.dbuser=dbconfigs['dbuser']
        self.dbpass=dbconfigs['dbpass']
        self.db=dbconfigs['db']

        self.assembly="GRCh37"

        self.connection = pymysql.connect(user=self.dbuser, 
                                          password=self.dbpass, 
                                          database=self.db,
                                          charset='utf8')
        self.connection.cursor().execute('SET SQL_MODE=ANSI_QUOTES')

        self.hdp = hgvs.dataproviders.uta.connect()
        self.parser = hgvs.parser.Parser()
        self.vm = hgvs.variantmapper.EasyVariantMapper(
            self.hdp, primary_assembly=self.assembly, alt_aln_method='splign')


        pass

    def transform(self, i, f, o, paper_id=None):
        h = o.split(",")
        d = None

        if f == 'parse_hgvs':
            d = self.convert_hgvs(i)
            
            if d['uncertain'] == True:
                raise RuntimeError("HGVS mapping reportedly 'uncertain' : '"+d['uncertain']+"'")

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

    def execdb(self, statement):            
        return self.connection.cursor().execute( statement )
        
    def fetch_rows(self, statement):
        return petl.fromdb(self.connection, statement)

    def parse_hgvs(self, var_c):
        """
        Returns chromosome:start-stop coordinates from a given HGVS variant string
        """
        var_g = self.vm.c_to_g(
            self.parser.parse_hgvs_variant(var_c)
        )
        return var_g

    def NC_to_chr(self, NC):
        return str(int(floor(float( 
            NC.replace("NC_", "").strip("0") 
        ))))

    def explode_hgvs(self, g):
        data = {}

        data['chr'] = self.NC_to_chr(g.ac)
        data['start'] = g.posedit.pos.start.base
        data['end'] = g.posedit.pos.end.base
        data['ref'] = g.posedit.edit.ref
        data['alt'] = g.posedit.edit.alt
        data['uncertain'] = g.posedit.uncertain

        return data

    def convert_hgvs(self, c):
        return self.explode_hgvs(self.parse_hgvs(c))

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
