import petl
import utils
from marvmodel import AbstractModel

class Annovar(AbstractModel):
    __properties_list = ['variant_id',
                         'genomicSuperDups',
                         'esp6500siv2_all',
                         '1000g2014oct_all',
                         '1000g2014oct_afr',
                         '1000g2014oct_eas',
                         '1000g2014oct_eur',
                         'snp138',
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

    def __init__(self, **kwargs):
        super(Annovar, self).__init__()
        self.sheet = "annovar"
        self.database_table= "annovar_score"

        self.propeties_list = Annovar._Annovar__properties_list

    def usage(self):
        print """
        Write usage
        """
