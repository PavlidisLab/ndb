import petl
import utils
from marvmodel import AbstractModel

class GeneRanks(AbstractModel):
    __properties_list = ['id',
                         'symbol',
                         'score',
                         'collection']


    def __init__(self, **kwargs):
        super(SFARI, self).__init__()

        self.database_table = "SFARI"
        self.properties_list = SFARI._SFARI__properties_list
        self.column_map = { "gene-symbol" : "symbol",
                            "gene-score" : "score" }


    def load(self, filename):
        self.data = petl.io.fromcsv(filename)

    
