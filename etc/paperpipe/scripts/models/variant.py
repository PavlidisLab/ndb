import sys
import petl
from collections import defaultdict

import utils
from marvmodel import AbstractModel
from rawvariant import RawVariant
from fixer import fixer as vf

class Variant(AbstractModel):
    __properties_list = "id,paper_id,raw_variant_id,event_id,subject_id,sample_id,chromosome,start_hg18,start_hg19,stop_hg19,ref,alt,gene,category,code_change,protein_change,good_mutation,gene_detail,func,aa_change,cytoband,denovo,lof".split(",")

    __excluded = "id".split(",")

    def __init__(self, paper_id, **kwargs):
        super(Variant, self).__init__()
        self.paper_id = paper_id

        self.sheet = "variant"
        self.database_table = self.DATABASE_TABLES['variant']

        self.properties_list = Variant._Variant__properties_list
        self.excluded = Variant._Variant__excluded

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
        raw_header = raw_variants[0]
        commit_header = []
        commit_data = []

        intersection =  self.U.intersection( RawVariant(self.paper_id).database_table,
                                             self.database_table,
                                             self.paper_id )


        for r in raw_variants[1:]:
            variant = {}

            # Transfer unmodified values
            for i in intersection:
                variant[raw_header[i]] = r[i]

            # Apply modifications
            for _transform in ifo:
                #transform = [z.strip() if z is not None else None for z in _transform]
                transform = [str(z).strip() for z in _transform if z is not None]
                #print _transform
                #print transform

                transform_input = None
                try:
                    "Attempt to get data from raw variant"
                    if transform[0] == 'NULL':
                        IDX=0
                    else:
                        IDX = raw_header.index( transform[0] )
                        transform_input = r[IDX]
                except:
                    "Use value directly instead of looking up raw variant"
                    transform_input = transform[0]

                h, d = self.U.transform( transform_input, transform[1], transform[2], paper_id = self.paper_id )

                for h,d in zip(h,d):
                    variant[h] = d

            ks = variant.keys()
            vs = []
            for k in ks:
                vs.append(variant[k])

            # Remove excluded fields or NoneTypes
            pops = []
            for i in range(len(vs)):
                if vs[i] is None or \
                   ks[i] in self.excluded:
                    pops.append(i)
            ks = [ks[x] for x in range(len(ks)) if x not in pops]
            vs = [vs[x] for x in range(len(vs)) if x not in pops]

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

        for i in range(len(_data)):
            header, row = _data[i]

            """
            Add possibly missing fields that will certainly be generated in this iteration.
            Includes: ref, alt, event_id
            """

            required = ['ref', 'alt', 'start_hg19', 'stop_hg19','chromosome']


            for req in required:
                if req not in header:
                    header.append(req)
                    row.append("")

            if EVENT_FIELD not in header:
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

            _before = [CHROMOSOME,START,STOP,REF,ALT]
            print "Fixing variant..."
            CHROMOSOME,START,STOP,REF,ALT = fixer.repair_variant(CHROMOSOME,START,STOP,REF,ALT)
            print "Fixed___________"
            
            if REF == ALT == "N":
                # Skipping variant because fixer could not fix.
                print "Could not fix:", _before
                print "Skipping"
                continue

            row[header.index('start_hg19')] = START
            row[header.index('stop_hg19')] = STOP
            row[header.index('chromosome')] = CHROMOSOME
            row[header.index('ref')] = REF
            row[header.index('alt')] = ALT


            existing_subject_id = self.get_subject_for_sample(row[SAMPLE_ID])
            print existing_subject_id
            if not existing_subject_id:
                existing_subject_id = "-1"
            row[SUBJECT_ID] = existing_subject_id


            events, subjects  = self.disambiguate_subjects(START,
                                                           STOP,
                                                           tolerance=3) # 3 because codons; one might consider them in the same event.
            """
            print events
            print SAMPLE_ID
            print events[row[SAMPLE_ID]]

            print subjects
            print SAMPLE_ID
            print subjects[row[SAMPLE_ID]]
            raw_input()
            """

            current_sample = row[SAMPLE_ID]
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
        for _data in self.data:
            try:
                predata = self.precommit([_data])
                if len(predata) < 1:
                    continue
                data = predata[0]
            except Exception as e:
                print "error caugth in commit()", _data
                print "Reason:", e.message
                raise e

            petl.appenddb(data, self.U.connection, self.database_table, commit=True)
        return self.data

    def get_subject_for_sample(self, sample):
        query = "SELECT DISTINCT subject_id FROM {0} WHERE sample_id = '{1}'  ;".format(self.database_table, sample)

        rows = self.U.fetch_rows(query)
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

    def disambiguate_subjects(self, start, stop, tolerance=0):
        """
        For a given variant, check that there's only one subject + contiguous variant(s) within range +- tolerance
        """
        for RANGE in range(0, tolerance + 1):
            print "RANGE:", RANGE
            RANGE_START_left = ( int(start) - int(RANGE) )
            RANGE_START_right = ( int(start) + int(RANGE) )

            RANGE_STOP_left = ( int(stop) - int(RANGE) )
            RANGE_STOP_right = ( int(stop) + int(RANGE) )


            query = "SELECT * FROM {0} WHERE start_hg19 >= {1} AND start_hg19 <= {2} AND stop_hg19 >= {3}  AND stop_hg19 <= {4} ;".format(self.database_table,
                                                                                                                                          RANGE_START_left,
                                                                                                                                          RANGE_START_right,
                                                                                                                                          RANGE_STOP_left,
                                                                                                                                          RANGE_STOP_right)

            #print query
            _rows = self.U.fetch_rows(query)
            header = None # Header fields
            rows = None # Actual data
            sample_ids = []
            event_ids = []
            sample_events = defaultdict(list)
            sample_subjects = defaultdict(list)

            if _rows:
                header = _rows[0]
                SID = header.index("sample_id")
                UID = header.index("subject_id")
                EID = header.index("event_id")
                rows = [row for row in _rows[1:]]
                # TODO: <- Check that event_id is paired with subject.
            #print "header/rows"
            #print header
            #print rows
            for r in rows:
                print "r", r
                sample_ids.append( r[SID] )
                event_ids.append( r[EID] )
                sample_events[r[SID]].append( r[EID] )
                sample_subjects[r[SID]].append( r[UID] )

            for sample in sample_events.keys():
                """
                Check that only one (sample -> subject) and only one (sample -> events)
                """
                subjs = set(sample_subjects[sample])
                events = set(sample_events[sample])
                if len(subjs) > 1:
                    raise Exception("Error: Multiple neighbouring variant subjects for same sample ID.")
                if len(events) > 1:
                    print "Multiple events", events, "for subjects", subjs
                    raise Exception("Error: Multiple contiguous variant events for same sample ID. This can be caused by duplicates in the import spreadsheet.")
            if len(sample_events.keys()) > 0:
                # Found a clustered event
                return sample_events, sample_subjects
            else:
                pass # Proceed with tolerance increase

        # Case where no ID was found
        return None, None

def bootstrap():
    # TODO: Remove for production
    variants = Variant(0)
    variants.load("../../exampledata/dyrk1a/variants.xlsx")
    variants.commit()
    return variants


if __name__ == "__main__":
    bootstrap()
