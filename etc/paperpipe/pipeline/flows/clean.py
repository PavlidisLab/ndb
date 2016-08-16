import hashlib
import luigi
import ast
import os 
import copy

# PaperPipe specific papers
from utils import Utils
from paper import Paper
from rawkv import RawKV
from rawvariant import RawVariant
from variant import Variant

def hash_filename(string):
    hash_object = hashlib.sha1(string)
    hex_dig = hash_object.hexdigest()
    return str(hex_dig)

class PPTask(luigi.Task):
    OUTPUT = None
    def __init__(self, book=None):
        super(PPTask, self).__init__(book=book)

    def dirty_set_paper_id(self, requirement):
        """
        Parse commit output text to get the ID. Assumes ID is the first column.
        """
        self.paper_id = -2
        requirement.output() # Set would-be requirement.OUTPUT name
        with open( requirement.OUTPUT, 'r') as f:
            for r in f:
                self.paper_id = ast.literal_eval(r)[0]
        return

    def set_paper_id_from_requirements(self, requirement):
        self.paper_id = requirement.paper_id        
        return



class LoadPaper(PPTask):
    OUTPUT = "commits/paper.out"
    book = luigi.Parameter()

    def __init__(self, book=None):
        super(LoadPaper, self).__init__(book=book)
        self.INPUT = book

    def input(self):
        #INPUT = "/home/mbelmadani/development/paperpipe/exampledata/dyrk1a/variants.xlsx"
        tag = hash_filename(self.INPUT)
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
        self.INPUT = book

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
        r.load(self.input())
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

    def run(self):
        if os.path.isfile(self.OUTPUT):
            print "PAPERPIPE: File already exists. Not running this task." # TODO: This should not be needed.
            return
        else:
            print "PAPERPIPE:", self.OUTPUT, " does not exist, apparently. "

        print "RUNNING LoadVariant Loader for Paper", self.paper_id
        
        raw = self.requires() # Obtain requirement.
        variant = Variant(self.paper_id)

        variant.load(self.input())
        data = variant.commit()

        with self.output().open('w') as f:
            for row in data:
                f.write(str(row))
                f.write("\n")


if __name__ == '__main__':
    luigi.run()
