import hashlib
import luigi
import ast
import os 
import copy
import simplejson as json
import operator

# PaperPipe specific papers
from utils import Utils
from paper import Paper
from rawkv import RawKV
from rawvariant import RawVariant
from variant import Variant
from annovar import Annovar

def hash_filename(string):
    hash_object = hashlib.sha1(string)
    hex_dig = hash_object.hexdigest()
    return str(hex_dig)

def blockable(fun):
    def _decorator(self, *args, **kwargs):
        if os.path.isfile(self.OUTPUT):
            print "PAPERPIPE: File already exists. Not running this task." 
            return
        else:
            print "PAPERPIPE:", self.OUTPUT, " does not appear to exist. "    
            print "RUNNING ", type(self), " Loader for Paper", self.paper_id

        return fun(self)
    return _decorator

class PPTask(luigi.Task):
    OUTPUT = None
    def __init__(self, book=None):
        super(PPTask, self).__init__(book=book)

    def clear_run(self, Class=None):
        requirement_commit = self.requires().OUTPUT
        if not os.path.exists(requirement_commit):
            print "PAPERPIPE: Import does not exist! Cannot run this.."
            return
        else:
            print "PAPERPIPE:", requirement_commit, " does exist, apparently. "

        try:
            print "RUNNING ", type(self).__name__ ," Deleter for Paper", self.paper_id
        except:
            # Must be Paper then.
            print "RUNNING ", type(self).__name__ ," Deleter for Paper import# ", hash_filename(os.path.basename(self.INPUT))

        rows = []
        with open( requirement_commit, 'r' ) as data:
            for row in data:
                rows.append(row)
        
        # Obtain requirement.
        raw = self.requires()
        
        try:
            model = Class(self.paper_id)
            model.load(self.input())
        except Exception as e:
            #print e
            #print "Task model =", str(type(Class)) #, " | ", "id = ", self.paper_id
            #if type(Class) == type(Paper):
            try:
                model = Class() # Must be Paper
            #else:
            except:
                model = Class(self.paper_id) # Must be Paper
            # Hack. Hax. Haque.
            # Issues with whitespaces when exporting JSON also.
            with open(requirement_commit, 'r') as f:
                text = ""
                for line in f:
                    text = line.replace("[", "") # Remove bracket
                    text = text.split(",")[0]

            paper_id_index = 0 # TODO: Hardcoded index 0 for IDs
            arr = json.loads(text)

            model.paper_id = text
            model.data = []

        data = model.delete()
        os.remove( requirement_commit ) #Clear file


    def clear_requires(self, requirement):
        # Requires call for the "Clear" (delete) version of the PPTask.0
        requirement_class_name = type(requirement).__name__
        check_requirement = copy.deepcopy(requirement)                
        
        REQUIREMENT_PATH = check_requirement.output().path

        if os.path.isfile(REQUIREMENT_PATH):
            print requirement_class_name, "commit exists."
            self.set_paper_id_from_requirements(check_requirement)
        else:
            print requirement_class_name, "does not exists."
        #    print REQUIREMENT_PATH, " DOES NOT exists! Aborting."
        #    exit(-1)

        self.input()
        #print "AFTER====>", self.paper_id
        return requirement


    def dirty_set_paper_id(self, requirement):
        """
        Parse commit output text to get the ID. Assumes ID is the first column.
        """
        self.paper_id = -2
        self.paper_key = None

        requirement.output() # Set would-be requirement.OUTPUT name
        HEADER = None
        ID_IDX = None
        KEY_IDX = None
        
        with open( requirement.OUTPUT, 'r') as f:
            for r in f:
                if HEADER is None:
                    #print "HEADER",HEADER
                    HEADER = ast.literal_eval(r)
                    ID_IDX = HEADER.index("id")
                    KEY_IDX = HEADER.index("paper_key")
                else:
                    ROW = ast.literal_eval(r)
                    self.paper_id = ROW[ID_IDX]
                    self.paper_key = ROW[KEY_IDX]
        return

    def set_paper_id_from_requirements(self, requirement):
        self.paper_id = requirement.paper_id        
        self.paper_key = requirement.paper_key
        return

class LoadPaper(PPTask):
    OUTPUT = "commits/paper.out"
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(LoadPaper, self).__init__(book=book)
        self.INPUT = book

    def input(self):
        #INPUT = "/home/mbelmadani/development/paperpipe/exampledata/dyrk1a/variants.xlsx"
        tag = hash_filename(os.path.basename(self.INPUT))
        LoadPaper.OUTPUT = str("commits/paper"+tag+".out")
        return self.INPUT
    
    def output(self):
        self.input() # Framework calls output before running tasks. This will set the output filename before checking if it exists.
        return luigi.LocalTarget( LoadPaper.OUTPUT )
    
    def run(self):
        # Load paper object
        p = Paper()
        p.update_from_file(self.input())

        # Commit to DB
        table = p.commit()
        self.pk = table[1][0] # Id assigned

        # Write changes to logs
        #with self.output(p.id).open('w') as f:
        with self.output().open('w') as f:
            for text in table:
                f.write(str(text))
                f.write("\n")

class LoadRawKV(PPTask):
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(LoadRawKV, self).__init__(book=book)
        self.paper_id = -1
        self.paper_key = None
        self.INPUT = book
        print "Initialized", type(self)

    def requires(self):
        requirement = LoadPaper(book=self.INPUT)
        check_requirement = copy.deepcopy(requirement)        
        if os.path.isfile(check_requirement.OUTPUT):
            self.dirty_set_paper_id(check_requirement)
        self.input()

        return requirement


    def input(self):
        #INPUT = "/home/mbelmadani/development/paperpipe/exampledata/dyrk1a/variants.xlsx"
        LoadRawKV.OUTPUT = str("commits/rkv_paper"+str(self.paper_id)+".out")
        return self.INPUT
    
    def output(self):
        self.input() # Framework calls output before running tasks. This will set the output filename before checking if it exists.
        return luigi.LocalTarget( LoadRawKV.OUTPUT )

    def run(self):
        if os.path.isfile(self.OUTPUT):
            print "PAPERPIPE: File already exists. Not running this task." # TODO: This should not be needed.
            return

        print "RUNNING RawKV Loader for Paper", self.paper_id
        r = RawKV(self.paper_id)

        print "Loading input for:", type(self)
        r.load(self.input())
        print "Input loaded"
        data = r.commit()

        with self.output().open('w') as f:
            for row in data:
                f.write(str(row))
                f.write("\n")

class LoadRawVariant(PPTask):
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(LoadRawVariant, self).__init__(book=book)
        self.paper_id = -1
        self.paper_key = None
        self.INPUT = book

    def requires(self):
        requirement = LoadRawKV(book=self.INPUT)
        check_requirement = copy.deepcopy(requirement)                
        if os.path.isfile(check_requirement.output().path):
            self.set_paper_id_from_requirements(check_requirement)
        self.input()

        return requirement

    def input(self):
        LoadRawVariant.OUTPUT = str("commits/rawvariant_paper"+str(self.paper_id)+".out")
        return self.INPUT
    
    def output(self):
        self.input() # Framework calls output before running tasks. This will set the output filename before checking if it exists.
        return luigi.LocalTarget( LoadRawVariant.OUTPUT )

    def run(self):
        if os.path.isfile(self.OUTPUT):
            print "PAPERPIPE: File already exists. Not running this task." # TODO: This should not be needed.
            return
        else:
            print self.OUTPUT, " does not exist, apparently. "

        print "RUNNING LoadRawVariant Loader for Paper", self.paper_id
        
        rkv = self.requires() # Obtain requirement.
        rawvariant = RawVariant(self.paper_id)
        rawvariant.set_rkv(rkv)

        rawvariant.load(self.input())
        data = rawvariant.commit()

        with self.output().open('w') as f:
            for row in data:
                f.write(str(row))
                f.write("\n")

class LoadVariant(PPTask):
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(LoadVariant, self).__init__(book=book)
        self.paper_id = -1
        self.INPUT = book

    def requires(self):
        requirement = LoadRawVariant(book=self.INPUT)
        check_requirement = copy.deepcopy(requirement)                
        if os.path.isfile(check_requirement.output().path):
            self.set_paper_id_from_requirements(check_requirement)
        self.input()

        return requirement

    def input(self):
        LoadVariant.OUTPUT = str("commits/variant_paper"+str(self.paper_id)+".out")
        return self.INPUT
    
    def output(self):
        self.input() # Framework calls output before running tasks. This will set the output filename before checking if it exists.
        return luigi.LocalTarget( LoadVariant.OUTPUT )

    @blockable
    def run(self):
        
        raw = self.requires() # Obtain requirement.
        variant = Variant(self.paper_id, paper_key=self.paper_key)

        variant.load(self.input())

        print "Variant data stats:"
        print "len", len(variant.data)
        print "Preview:", variant.data[:10]

        print " Preparing to commit ... "

        try:
            data = variant.commit()
        except Exception as e:
            print e
            print e.message
            raise e 

        with self.output().open('w') as f:
            for row in data:
                f.write(str(row))
                f.write("\n")

class LoadAnnovar(PPTask):
    book = luigi.Parameter()

    """
    Load Annovar by copy of the Variant data.
    """
    def __init__(self, book=None):
        super(LoadAnnovar, self).__init__(book=book)            
        self.paper_id = -1
        self.INPUT = book

    def input(self):
        LoadAnnovar.OUTPUT = str("commits/annovar_paper"+str(self.paper_id)+".out")
        return self.INPUT
    
    def output(self):
        self.input() # Framework calls output before running tasks. This will set the output filename before checking if it exists.
        return luigi.LocalTarget( LoadAnnovar.OUTPUT )

    def requires(self):
        requirement = LoadVariant(book=self.INPUT)
        check_requirement = copy.deepcopy(requirement)                
        if os.path.isfile(check_requirement.output().path):
            self.set_paper_id_from_requirements(check_requirement)
        self.input()
        return requirement

    @blockable
    def run(self):
        """
        Run annovar for all variants in this paper.
        """
        annovar = Annovar(self.paper_id)
        annovar.load( self.input() )
        data = annovar.commit()    
        
        with self.output().open('w') as f:
            for row in data:
                f.write(str(row))
                f.write("\n")


class LoadCurationNotes(PPTask):
    book = luigi.Parameter()

    """
    Load curation notes.
    """
    def __init__(self, book=None):
        super(LoadCurationNotes, self).__init__(book=book)            
        self.paper_id = -1
        self.INPUT = book

    def input(self):
        LoadCurationNotes.OUTPUT = str("commits/curationnotes_paper"+str(self.paper_id)+".out")
        return self.INPUT
    
    def output(self):
        self.input() # Framework calls output before running tasks. This will set the output filename before checking if it exists.
        return luigi.LocalTarget( LoadCurationNotes.OUTPUT )

    def requires(self):
        requirement = LoadPaper(book=self.INPUT)
        check_requirement = copy.deepcopy(requirement)        
        if os.path.isfile(check_requirement.OUTPUT):
            self.dirty_set_paper_id(check_requirement)
        self.input()
        return requirement

    @blockable
    def run(self):
        """
        Update curation notes based on paper ID
        """
        print "Paper ID is", self.paper_id

        # TODO: Use subprocess or make a model object for this (consistently with other tasks.)
        STRING = "sh pushCurationNotes.sh " + self.INPUT +  " " + str(self.paper_id)
        print("Processing: " + STRING)
        os.system( STRING )

            
class ClearAnnovar(LoadAnnovar):
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(ClearAnnovar, self).__init__(book=book)
        self.paper_id = -1
        self.INPUT = book

    def requires(self):
        requirement = self.clear_requires(LoadAnnovar(book=self.INPUT))
        return requirement

    def run(self):
        self.clear_run(Class=Annovar)

class ClearVariant(LoadVariant):
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(ClearVariant, self).__init__(book=book)
        self.paper_id = -1
        self.INPUT = book

    def requires(self):
        requirement = self.clear_requires(LoadVariant(book=self.INPUT))
        return requirement

    def run(self):
        self.clear_run(Class=Variant)

class ClearRawVariant(LoadRawVariant):
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(ClearRawVariant, self).__init__(book=book)
        self.paper_id = -1
        self.INPUT = book

    def requires(self):
        requirement = self.clear_requires(LoadRawVariant(book=self.INPUT))
        return requirement

    def run(self):
        self.clear_run(Class=RawVariant)

class ClearRawKV(LoadRawKV):
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(ClearRawKV, self).__init__(book=book)
        self.paper_id = -1
        self.INPUT = book

    def requires(self):
        requirement = self.clear_requires(LoadRawKV(book=self.INPUT))
        return requirement

    def run(self):
        self.clear_run(Class=RawKV)

class ClearPaper(LoadPaper):
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(LoadPaper, self).__init__(book=book)
        self.INPUT = book
        self.input()
        self.run()

    def requires(self):
        requirement = LoadPaper(book=self.INPUT)
        return requirement

    def run(self):
        self.clear_run(Class=Paper)

if __name__ == '__main__':
    luigi.run()
