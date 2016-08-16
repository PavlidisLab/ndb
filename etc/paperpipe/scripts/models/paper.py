import petl
import utils
from marvmodel import AbstractModel

class Paper(AbstractModel):
    __properties_list = ['id',
                         'author',
                         'paper_table',
                         'mut_reporting',
                         'scope',
                         'cohort_source',
                         'reported_effects',
                         'doi',
                         'title',
                         'paper_key',
                         'publisher',
                         'year',
                         'cases',
                         'count',
                         'design']
    
    def __init__(self, **kwargs):
        super(Paper, self).__init__()
        self.sheet = "paper"
        self.database_table= self.DATABASE_TABLES["papers"]

        self.id = self.get_biggest_ID(table=self.database_table)
        self.properties_list =  Paper._Paper__properties_list

    #def get_biggest_ID(self, ID="id"):
    #    super(Paper, self).get_biggest_ID(ID=ID)

    def delete(self):
        for _data in self.data:
            pass # We can do something more sophisticated, but for now, deleting by ID should be fine
        self.U.delete_table_rows_by_id(self.database_table, self.paper_id)        
        return self.data

    
    def usage(self):
        print """
        a = Paper()
        a.update_from_file("../exampledata/dyrk1a/variants.xlsx")
        
        print dir(Paper)
        print dir(a)
        
        a.commit()
        """

