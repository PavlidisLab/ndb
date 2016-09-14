import petl
import utils

class AbstractModel(object):
    U = utils.Utils()
    
    
    def __init__(self, **kwargs):

        self.DATABASE_TABLES = {}
        
        #suffix = "_test" # Use suffix like _test to use the proper table version. Leave blank for production.
        suffix = ""

        for table in [ 'papers',
                       'raw_key_value',
                       'raw_variant',
                       'variant',
                       'annovar_scores', ]:
        
            self.DATABASE_TABLES[table] = table + suffix
        
        self.sheet=None
        self.data=None
        self.database_table=None
        self.properties_list = []
        [self.__setattr__(key, kwargs.get(key)) for key in self.properties_list]

        
    def update_from_file(self, filename):
        key = None
        value = None
        paper = self.U.load_paper(filename, sheet=self.sheet)
        
        i=0
        for row in paper:
            key,value = row
            self.__setattr__(key, value)

    def load_sheet(self, filename, sheet=None, range_string=None, row_offset=0, column_offset=0, **kwargs):
        _content = petl.io.xlsx.fromxlsx(filename, sheet, range_string, row_offset=0, column_offset=0, **kwargs)
        content = []
        for c in _content:
            if not any(c):
                break
            else:
                content.append(c)
        return content

    def get_biggest_ID(self, ID="id", table=None):
        #TODO: Should be from Utils class
        """
        Gets the biggest available "ID" of this model type.
        TODO: This should be rename to something like get next ID, or get available ID.
        """
        if table == None:
            database_table = self.database_table
        else:
            database_table = table

        max_ID = petl.fromdb(self.U.connection, 'SELECT MAX('+ID+') FROM ' + database_table + ' ;'  )
        pk = 0
        if max_ID[1][0] is not None:
            pk = max_ID[1][0] + 1

        return pk

    def commit(self):
        """
        Push model's to append to the database
        """
        table = [
            [key for key in self.properties_list] ,
            [getattr(self, key) for key in self.properties_list]
        ]
        petl.appenddb(table, self.U.connection, self.database_table )

        return table

    def delete(self):
        for _data in self.data:
            pass # We can do something more sophisticated, but for now, deleting by ID should be fine
            #print "============================"
            #print _data
            #data = self.precommit([_data])[0]
            #(data, self.U.connection, self.database_table, commit=True)
            #print data
            #print "============================"
        self.U.delete_table_rows_by_paper(self.database_table, self.paper_id)        
        return self.data

    def usage(self):
        print """
        Abstract Model shouldn't be instancized.
        """

