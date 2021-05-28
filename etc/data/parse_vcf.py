#!/usr/bin/env python
# coding: utf-8

# ## Import and data load

# In[89]:


import pandas as pd
import numpy as np
from copy import copy 


# In[90]:


def info_header( x:str,y:str,z:str ) -> str :
    ''' Generate the info header (only supporting ID, Type and Description at this stage.)'''
    return '##INFO=<ID="{ID}",Type="{Type}",Description="{Description}">'.format(ID=x.strip(),
                                                                                 Type=y.strip(),
                                                                                 Description=z.strip()
                                                                         )
def generate_info(header : list, row : list) -> str:
    ''' Map the info fields into a semicolon separated key=value string. '''
    return( ";".join(["{k}={v}".format(k=k,v=v) for k,v in zip(header,row)]) )

def get_vcfdate() -> str:
    now = datetime.now()
    VCF_DATE = str(now.date()).replace("-","")
    return VCF_DATE


# In[91]:


VARICARTA_VERSION="1.1"

OUTPUT_FILE="../out/export_{VARICARTA_VERSION}.vcf".format(VARICARTA_VERSION=VARICARTA_VERSION)

VCF_HEADER='''##fileformat=VCFv4.3
##fileDate={VCF_DATE}
##source=VariCarta{VARICARTA_VERSION}
'''.format(VCF_DATE=get_vcfdate(),
           VARICARTA_VERSION=VARICARTA_VERSION
          )


# ### Input data
# Put the latest or preferred varicarta export in this notebook's directory.

# In[92]:


variant_data = pd.read_csv("../res/export_latest.tsv", sep="\t")
info_fields = pd.read_csv("../res/vcf_info.tsv", sep="\t")


# In[93]:


''' Generate INFO headers'''
INFO_HEADER : list = info_fields.apply(lambda x : info_header(x[0], x[2], x[3]), axis=1)


# In[94]:


''' Map required columns '''
COLS_USED = {
    'chromosome' : '#CHROM',
    'start_hg19' : 'POS',
    'event_id' : 'ID',
    'ref' : 'REF',
    'alt' : 'ALT'    
}

''' Get rest of data for info columns'''
INFO_COLS = sorted(list(set(variant_data.columns) - set(COLS_USED.keys())))

''' Subset dataframe for required cols and rename '''
df_vcf = copy(variant_data[COLS_USED.keys()])
df_vcf = df_vcf.rename(columns = COLS_USED)

''' Insert empty fields. Could be used for validation or sequencing type'''
df_vcf["QUAL"] = "."
df_vcf["FILTER"] = "."

''' Insert INFO fields'''
df_vcf["INFO"] = variant_data[INFO_COLS].apply(lambda x : generate_info(header=INFO_COLS, row=x), axis=1)


# In[95]:


''' Clear file '''
f = open(OUTPUT_FILE, 'w')
f.write(VCF_HEADER) # Write header
f.write("\n".join(INFO_HEADER) + "\n") # Write header
f.write("\t".join(df_vcf.columns.tolist()) + "\n") # Write header
df_vcf.to_csv(f, sep="\t", header=False, index=False)
f.close()

