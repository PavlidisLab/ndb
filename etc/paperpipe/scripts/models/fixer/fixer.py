from subprocess import check_output, CalledProcessError

class Fixer(object):
    def get_ref(self, chromosome, start, stop):
        start_str = "-start={0}".format(str(start - 1))  # 0 based
        stop_str = "-end={0}".format(str(stop))  # Don't remove 1 here because it is [start,stop)
        chromosome_str = "-seq=chr{0}".format(chromosome)
        if chromosome_str == "-seq=chr23":
            chromosome_str = "-seq=chrX"
        elif chromosome_str == "-seq=chr24":
            chromosome_str = "-seq=chrY"
        exe = "/home/mbelmadani/development/ndb/etc/paperpipe/scripts/models/fixer/twoBitToFa"
        genome = "/home/mbelmadani/development/ndb/etc/paperpipe/scripts/models/fixer/hg19.2bit"

        fix =  check_output(  [ exe, genome, 'stdout', chromosome_str, start_str, stop_str ]  )
        return "".join( fix.split('\n')[1:] )

    def repair_variant(self, chromosome, start, stop, ref, alt):
        """
        Repairs incomplete variant report.
        1 - If ref and alt are none, then error, we can't repair this.
        2 - If ref is empty but not alt (insertion), then decrease start position by 1, and prepend base to ref and alt
        3 - If alt is empty but not ref (deletion), the decreate start position by 1, and prepend base to ref and alt.
        4 - If stop is missing, then compute using the distance from start + ref

        ---- Otherwise, don't touch it, but run sanity check anyways.

        """
        start_, ref_, alt_ = start, ref, alt
        stop_ = stop

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
            anchor = self.get_ref(chromosome, start_, start_)

            alt_ = anchor + alt_
            # Get rid of non-nucleotides
            ref_ = anchor
            stop_ = None # Needs to be reset

        elif (alt in empty):
            """ Variant needs an anchor. _ b => p pb"""
            start_ = start_ - 1
            anchor = self.get_ref(chromosome, start_, start_)

            ref_ = anchor + ref_
            # Get rid of non-nucleotides
            alt_ = anchor
            stop_ = None # Needs to be reset

        if stop_ in [None, '', 0]:
            stop_ = start_ + (len(ref_) - 1)

        try:
            #Sanity check that variant ref matches reference genome.
            expected = self.get_ref(chromosome, start_, stop_)
            if expected != ref_:
                raise ValueError("Excepted is not the same between reference genome and current variant: " + expected + " v.s. " + ref_)        
        except ValueError as v:
            print v
            print "Something went wrong when trying to match reference variant to reference genome."
            print "chromosome:", chromosome
            print "start:", start_
            print "stop:", stop_
            print "ref:", ref_
            print "alt:", alt_
            raise v

        # Everything looks good.
        return chromosome, start_, stop_, ref_, alt_

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
