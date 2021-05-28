#!/usr/bin/env python

import pandas as pd
import numpy as np
from copy import copy 
from datetime import datetime
from io import StringIO
import re

def info_header( x:str,y:str,z:str ) -> str :
    ''' Generate the info header (only supporting ID, Type and Description at this stage.)'''
    return '##INFO=<ID={ID},Number=1,Type={Type},Description="{Description}">'.format(ID=x.strip(),
                                                                                 Type=y.strip(),
                                                                                 Description=z.strip()
                                                                         )
def sanitize_info_value(v):
    if type(v) == str:
        return re.sub('[,;="]', '', v)
    elif pd.isna(v):
        return '.'
    else:
        return v

def generate_info(header : list, row : list) -> str:
    ''' Map the info fields into a semicolon separated key=value string. '''
    return( ";".join(["{k}={v}".format(k=k,v=sanitize_info_value(v)) for k,v in zip(header,row)]) )

def get_vcfdate() -> str:
    now = datetime.now()
    VCF_DATE = str(now.date()).replace("-","")
    return VCF_DATE

def vcf_type_from_dtype(col, dtype):
    if dtype == 'float64':
        vcf_type = 'Float'
    elif dtype == 'int64':
        vcf_type = 'Integer'
    else:
        vcf_type = 'String'
    return vcf_type

VARICARTA_VERSION="latest"

OUTPUT_FILE="../out/export_{VARICARTA_VERSION}.vcf".format(VARICARTA_VERSION=VARICARTA_VERSION)

VCF_HEADER='''##fileformat=VCFv4.3
##fileDate={VCF_DATE}
##source=VariCarta{VARICARTA_VERSION}
'''.format(VCF_DATE=get_vcfdate(),
           VARICARTA_VERSION=VARICARTA_VERSION
          )

variant_data = pd.read_csv("../out/export_latest.txt", sep="\t")

# Map required columns
COLS_USED = {
    'chromosome' : '#CHROM',
    'start_hg19' : 'POS',
    'event_id' : 'ID',
    'ref' : 'REF',
    'alt' : 'ALT'    
}

# Get rest of data for info columns
INFO_COLS = sorted(list(set(variant_data.columns) - set(COLS_USED.keys())))

INFO_HEADER = [info_header(col, vcf_type_from_dtype(col, variant_data.dtypes[col]), col) for col in INFO_COLS]

# Subset dataframe for required cols and rename
df_vcf = copy(variant_data[COLS_USED.keys()])
df_vcf = df_vcf.rename(columns = COLS_USED)

# Insert empty fields. Could be used for validation or sequencing type
df_vcf["QUAL"] = "."
df_vcf["FILTER"] = "."

# Insert INFO fields
df_vcf["INFO"] = variant_data[INFO_COLS].apply(lambda x : generate_info(header=INFO_COLS, row=x), axis=1)

with open(OUTPUT_FILE, 'w') as f:
    f.write(VCF_HEADER) # Write header
    f.write("\n".join(INFO_HEADER) + "\n") # Write header
    df_vcf.sort_values(['#CHROM', 'POS']).to_csv(f, sep="\t", index=False)
