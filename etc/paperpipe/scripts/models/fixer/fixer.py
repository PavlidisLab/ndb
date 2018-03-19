from subprocess import check_output, CalledProcessError
import os
import sys

class Fixer(object):

    def __init__(self):
        self.exe = "../scripts/models/fixer/twoBitToFa"
        self.genome = "../scripts/models/fixer/hg19.2bit"

        if not (os.path.isfile(self.exe) and os.path.isfile(self.genome)):
            raise Exception("Error from "+ sys.argv[0] + ": twoBitToFa and hg19.2bit are not accessible in the fixer directory.")
    
    def get_ref(self, chromosome, start, stop):

        start_str = "-start={0}".format(str(start - 1))  # 0 based
        stop_str = "-end={0}".format(str(stop))  # Don't remove 1 here because it is [start,stop)
        chromosome_str = "-seq=chr{0}".format(chromosome)
        if chromosome_str == "-seq=chr23":
            chromosome_str = "-seq=chrX"
        elif chromosome_str == "-seq=chr24":
            chromosome_str = "-seq=chrY"

        fix =  check_output(  [ self.exe, self.genome, 'stdout', chromosome_str, start_str, stop_str ]  )
        return "".join( fix.split('\n')[1:] )

    def repair_variant(self, chromosome, start, stop, ref, alt):
        """
        Repairs incomplete variant report.
        1 - If ref and alt are none, then error, we can't repair this.
        2 - If ref is empty but not alt (insertion), then decrease start position by 1, and prepend base to ref and alt
        3 - If alt is empty but not ref (deletion), the decreate start position by 1, and prepend base to ref and alt.
        4 - If stop is missing, then compute using the distance from start + length(ref)
        5 - If start is missing. then compute using the distance from stop - length(ref)
        ---- Otherwise, don't touch it, but run sanity check anyways.

        """
        print "BEFORE", chromosome,":",start,stop,ref,alt
        start_, ref_, alt_ = start, ref, alt
        stop_ = stop
        chromosome_ = chromosome.replace("chr", "").replace("23", "X").replace("24", "Y")
        
        empty = [None, '', "-", "0"]

        if ref == alt and ref in empty:
            """ The unrepairable case """
            print "Error: ref and alt cannot both be empty..."
            raise ValueError
        elif ref == alt and ref == "N":
            """ N == ref == alt means that we have to skip this variant."""
            return None, None, None, ref, alt
        elif (ref in empty):
            """Variant need an anchor a _ => pa p"""
            start = start_ -1
            anchor = self.get_ref(chromosome_, start_, start_)

            alt_ = anchor + alt_
            # Get rid of non-nucleotides
            ref_ = anchor
            stop_ = None # Needs to be reset

        elif (alt in empty):
            """ Variant needs an anchor. _ b => p pb"""
            start_ = start_ - 1
            anchor = self.get_ref(chromosome_, start_, start_)

            ref_ = anchor + ref_
            # Get rid of non-nucleotides
            alt_ = anchor
            stop_ = None # Needs to be reset

        if stop_ in empty: #[None, '', 0]:
            stop_ = int(start_) + (len(ref_) - 1 )
        elif start_ in empty:
            start_ = int(stop_) - (len(ref_) - 1 )
        
        ref_ = ref_.upper()
        alt_ = alt_.upper()

        print "AFTER", chromosome_,":",start_,stop_,ref_,alt_
        
        try:
            #Sanity check that variant ref matches reference genome.
            expected = str(self.get_ref(chromosome_, start_, stop_)).upper()
            ref_ = ref_.upper()
            if expected != ref_:
                #raise ValueError("Expected is not the same between reference genome and current variant: " + expected + " v.s. " + ref_ + " for location "+ str(chromosome)+":"+str(start_)+"-"+str(stop_))
                print "Expected is not the same between reference genome and current variant: " + expected + " v.s. " + ref_ + " for location "+ str(chromosome_)+":"+str(start_)+"-"+str(stop_)
                reply = raw_input()
                if reply == 'q':
                    print "Terminated by user."
                    exit(-1)
        except ValueError as v:
            print v
            print "Something went wrong when trying to match reference variant to reference genome."
            print "chromosome:", chromosome_
            print "start:", start_
            print "stop:", stop_
            print "ref:", ref_
            print "alt:", alt_
            raise v

        # Everything looks good.
        return chromosome_, start_, stop_, ref_, alt_

if __name__ == "__main__":
    import sys

    args = sys.argv[1:]
    f = Fixer()
    if len(args) < 5:
        print "Usage:"
        print sys.argv[0], "chromosome start stop ref alt"
        sys.exit(-len(args))

    chromosome, start, stop, ref, alt = args

    print "Before:", args
    print "After:", f.repair_variant(chromosome, int(start), int(stop), ref, alt)
