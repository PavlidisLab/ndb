import shutil
import os
import sys
import petl
from collections import defaultdict
from itertools import groupby

import utils

from marvmodel import AbstractModel
from rawvariant import RawVariant
from fixer import fixer as vf

class Variant(AbstractModel):
    __properties_list = "id,paper_id,raw_variant_id,event_id,subject_id,sample_id,chromosome,start_hg18,start_hg19,stop_hg19,ref,alt,gene,category,code_change,protein_change,good_mutation,gene_detail,func,aa_change,cytoband,lof,inheritance,validation_method,validation_reported,validation".split(",")

    __excluded = "id".split(",")

    def __init__(self, paper_id, paper_key = None, **kwargs):
        super(Variant, self).__init__()
        self.paper_id = paper_id
        self.paper_key = paper_key

        self.sheet = "variant"
        self.database_table = self.DATABASE_TABLES['variant']

        self.properties_list = Variant._Variant__properties_list
        self.excluded = Variant._Variant__excluded
        self.LOGDIR="variant_log/"
        self.LOGFILE=self.LOGDIR+str(self.paper_id)+".log"        

        if os.path.exists(self.LOGFILE):
            os.remove(self.LOGFILE)

        if not os.path.exists(self.LOGDIR):
            os.makedirs(self.LOGDIR, 0775)
        

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
        print "Fetched", len(raw_variants), "raw variants."
        raw_header = raw_variants[0]
        commit_header = []
        commit_data = []

        intersection =  self.U.intersection( RawVariant(self.paper_id).database_table,
                                             self.database_table,
                                             self.paper_id )

        print "Intersection: ", len(intersection), "raw variants."


        for r in raw_variants[1:]:
            variant = {}

            # Transfer unmodified values
            for i in intersection:
                variant[raw_header[i]] = r[i]

            # Apply modifications
            for _transform in ifo:
                #transform = [z.strip() if z is not None else None for z in _transform]
                # Obtain transformation input, function and output (elem 0,1,2)
                transform = [str(z).strip() for z in _transform if z is not None]
                #print _transform
                #print transform

                transform_input = None
                try:
                    # Attempt to get data from raw variant
                    if transform[0] == 'NULL':
                        IDX=0
                    else:                        
                        if transform[0] in raw_header:
                            IDX = raw_header.index( transform[0] )
                            transform_input = r[IDX]
                        else:
                            # Use value directly instead of looking up raw variant
                            transform_input = transform[0]

                except Exception as e:
                    eprint("Error! Transform failed")
                    raise e                    

                # Apply transformation
                h, d = self.U.transform( transform_input, transform[1], transform[2], paper_id = self.paper_id, paper_key = self.paper_key )

                for h_,d_ in zip(h,d):
                    variant[h_] = d_

            ks = variant.keys()
            vs = []
            for k in ks:
                vs.append(variant[k])

            # Remove excluded fields or NoneTypes
            pops = []
            for i in xrange(len(vs)):
                if vs[i] is None or \
                   ks[i] in self.excluded:
                    pops.append(i)

            ks = [ks[x] for x in xrange(len(ks)) if x not in pops]
            vs = [vs[x] for x in xrange(len(vs)) if x not in pops]

            if self.data is None:
                self.data = []
            self.data.append([ ks, vs])
        ###########################################

    def precommit(self, _data):
        print "Precommiting..."
        data = []
        SUBJECT_FIELD = 'subject_id'
        SAMPLE_FIELD = 'sample_id'
        EVENT_FIELD = 'event_id'
        
        print "Precommit len:", len(_data)
        for i in xrange(len(_data)):
            header, row = _data[i]

            """
            Add possibly missing fields that will certainly be generated in this iteration.
            Includes: ref, alt, event_id
            """

            required = ['ref', 'alt', 'start_hg19', 'stop_hg19','chromosome']

            for req in required:
                # If any of the required fields are missing, append them to the header and and initialize the row to ""
                if req not in header:                    
                    header.append(req)
                    row.append("")

            if EVENT_FIELD not in header:
                # Initialize event field if missing
                header.append(EVENT_FIELD)
                row.append("-1")

            SUBJECT_ID = header.index(SUBJECT_FIELD)
            SAMPLE_ID = header.index(SAMPLE_FIELD)
            EVENT_ID = header.index(EVENT_FIELD)

            START = row[header.index('start_hg19')]
            STOP = row[header.index('stop_hg19')]
            CHROMOSOME = row[header.index('chromosome')]
            ALT = row[header.index('alt')]
            REF = row[header.index('ref')]

            print "Initializing Fixer."
            fixer = vf.Fixer()
            old = "{}:{}-{}{}>{}".format(*[x if type(x) != str or len(x) > 0 else "None" for x in [CHROMOSOME,START,STOP,REF,ALT]] )

            print "Fixing variant..."
            CHROMOSOME,START,STOP,REF,ALT = fixer.repair_variant(CHROMOSOME,START,STOP,REF,ALT)
            new = "{}:{}-{}{}>{}".format(CHROMOSOME,START,STOP,REF,ALT)
            print "Fixed",old,"to",new
            
            try:
                if REF == ALT == "N":
                    # Skipping variant because fixer could not fix.
                    print "Could not fix:", old
                    print "Skipping"
                    with open(self.LOGFILE, 'a') as f:
                        f.write("Could not fix", _data[i])
                        f.write("\n")
                        continue
                else:
                    with open(self.LOGFILE, 'a') as f:
                        f.write("Fixed "+old+" to "+new) 
                        f.write("\n")

            except Exception as e:
                print "Failure trying to write to log file."
                raise e
                
            row[header.index('start_hg19')] = START
            row[header.index('stop_hg19')] = STOP
            row[header.index('chromosome')] = CHROMOSOME
            row[header.index('ref')] = REF
            row[header.index('alt')] = ALT

            current_sample = row[SAMPLE_ID].strip() # Remove whitespaces as they can be hard to spot in the spreadsheet..
            existing_subject_id = self.get_subject_for_sample(current_sample)
            print "Existing subject id for this subject:", existing_subject_id

            if existing_subject_id is None:
                existing_subject_id = "-1"
            row[SUBJECT_ID] = existing_subject_id

            
            # Find variant events that cluster at distance tolerance with this variant
            # Return sampleid -> events, subjects dict tuple
            events, subjects  = self.disambiguate_subjects(CHROMOSOME,
                                                           START,
                                                           STOP,
                                                           tolerance=1) # Immediately adjacent or else nothing.


            
            if events and len(events[current_sample]) > 0:
                row[EVENT_ID] = events[current_sample][0]

            if str(row[EVENT_ID]) == "-1":
                row[EVENT_ID] = self.get_biggest_ID(EVENT_FIELD, table=self.database_table)

            if str(row[SUBJECT_ID]) == "-1":
                row[SUBJECT_ID] = self.get_biggest_ID(SUBJECT_FIELD, table=self.database_table)

            print "Adding data to row:", row
            data.append([header, row])

        print "Precommit done!"
        return data

    def commit(self):
        print "Len of data:", len(self.data)
        print "Preview"
        print self.data[:10]


        countLine = 0
        for _data in self.data: # Process data row by row
            # Currently, each row has the header and the data as element 0 and 1

            countLine += 1
            print "Current row:", countLine
            try:
                predata = None
                predata = self.precommit([_data])
                if len(predata) < 1:
                    continue
                data = predata[0]
            except Exception as e:
                print "error caught in precommit()", _data
                print "predata:", predata
                print "Reason:", e.message
                raise e

            petl.appenddb(data, self.U.connection, self.database_table, commit=True)

        return self.data

    def get_subject_for_sample(self, sample):
        try : 
            query = "SELECT DISTINCT subject_id FROM {0} WHERE sample_id = '{1}'  ;".format(self.database_table, sample)
        except:
            try: 
                query = u"SELECT DISTINCT subject_id FROM {0} WHERE sample_id = '{1}'  ;".format(self.database_table, sample)
            except Exception as e:
                print "Error generating query to:" # ascii characters cause this issue sometimes
                print "Database table", self.database_table
                print "Using sample:", sample

                
        print "QUERY",query
        rows = self.U.fetch_rows(query)
        print "ROWS", rows

        subject = "-1"
        if len(rows) > 2: # [0] => Header [1:] => data
            print "Problem:"
            print rows
            raise Exception("There should not be more than one subject ID for a given sample ID. sample_id = " + str(sample))
        elif len(rows) < 2:
            pass # No known IDs yet
        else:
            subject = rows[1][0]
        return subject

    def disambiguate_subjects(self, chromosome, start, stop, tolerance=1):
        """
        For a given variant, check that there's only one subject + contiguous variant(s) within range +- tolerance
        """
        
        sample_events, sample_subjects = defaultdict(list),defaultdict(list) # Return values
        
        # for RANGE in xrange(0, tolerance + 1):
        for RANGE in [tolerance]: # FIXME: Don't need to check all, just the biggest tolerance.
            print "RANGE:", RANGE
            RANGE_left = ( int(start) - int(RANGE) )
            RANGE_right = ( int(stop) + int(RANGE) )

            #RANGE_STOP_left = ( int(stop) - int(RANGE) )
            #RANGE_STOP_right = ( int(stop) + int(RANGE) )


            query = "SELECT * FROM {0} WHERE ((start_hg19 >= {1} AND start_hg19 <= {2}) OR (stop_hg19 >= {3}  AND stop_hg19 <= {4})) AND chromosome='{5}' ;".format(self.database_table,
                                                                                                                                          RANGE_left,
                                                                                                                                          RANGE_right,
                                                                                                                                          RANGE_left,
                                                                                                                                          RANGE_right,
                                                                                                                                          chromosome)
            _rows = self.U.fetch_rows(query)
            header = None # Header fields
            rows = None # Actual data
            sample_ids = []
            event_ids = []
            sample_events = defaultdict(list)
            sample_subjects = defaultdict(list)
            sample_start = defaultdict(list)
            sample_stop = defaultdict(list)

            if _rows:
                header = _rows[0]
                SID = header.index("sample_id")
                UID = header.index("subject_id")
                EID = header.index("event_id")
                STARTPOS = header.index("start_hg19")
                STOPPOS = header.index("stop_hg19")
                rows = [row for row in _rows[1:]]

            for r in rows:
                print "Ambiguous subject:", r
                sample_ids.append( r[SID] )
                event_ids.append( r[EID] )
                sample_events[r[SID]].append( r[EID] )
                sample_subjects[r[SID]].append( r[UID] )
                sample_start[r[SID]].append( r[STARTPOS] )
                sample_stop[r[SID]].append( r[STOPPOS] )

            for sample in sample_events.keys():
                """
                Check that only one (sample -> subject) and only one (sample -> events)
                """
                subjs = set(sample_subjects[sample])
                events = set(sample_events[sample])
                if len(subjs) > 1:
                    print "Multiple subjects", subjs, "for events", events
                    raise Exception("Error: Multiple neighbouring variant subjects for same sample ID.")
                if len(events) > 1:
                    print "Found more than one event for " + sample
                    indices = [i for i, x in enumerate(event_ids) if x in events]
                    ranges = []
                    
                    print len(indices), "indices to test."
                    for index in indices:
                        print "adding index", index
                        ranges += xrange(sample_start[sample][index], sample_stop[sample][index] + 1 )

                    found_overlap = ([len(list(group)) > 1 for key, group in groupby(ranges)])

                    print "Overlap computed."
                    if ( any(found_overlap) ):                                            
                        print "Multiple events", events, "for subjects", subjs
                        raise Exception("Error: Multiple contiguous variant events for same sample ID. This can be caused by duplicates in the import spreadsheet.")
                    else:
                        print "Found "+ str(sum(found_overlap)) +" `event_id`s for "+sample+" but they are non-overlapping. This is rare but possible (two previous variants with a gap contiguous with the current variant.)"
                        print "Check beween", str(min(ranges)), "and", str(max(ranges)), "."
                        print "Is this correct? [OK/(q)uit]"
                        resp = raw_input()
                        if resp in ("q", "qu", "qui", "quit"):
                            print "User requested halt."
                            Exception("User requested halt.")
                        else:
                            print "Allowed."


            if len(sample_events.keys()) > 0:
                # Found a clustered event
                # print "Breaking at tolerance:", tolerance
                break                
            else:
                # print "Not breaking at tolerance:", tolerance
                # Proceed with tolerance increase
                pass 

        return sample_events, sample_subjects #None, None

def bootstrap():
    # TODO: Remove for production
    variants = Variant(0)
    variants.load("../../exampledata/dyrk1a/variants.xlsx")
    variants.commit()
    return variants


if __name__ == "__main__":
    print "Uncomment bootstrap() "
    # bootstrap()
