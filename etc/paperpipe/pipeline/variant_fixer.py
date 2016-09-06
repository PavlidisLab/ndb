from subprocess import check_output, CalledProcessError


def get_ref(chromosome, start, stop):
    start_str = "-start={0}".format(str(start - 1))  # 0 based
    stop_str = "-end={0}".format(str(stop))  # Don't remove 1 here because it is [start,stop)
    chromosome_str = "-seq=chr{0}".format(chromosome)
    exe = "/home/mbelmadani/development/ndb/etc/paperpipe/pipeline/twoBitToFa"
    fix =  check_output(  [ exe, 'hg19.2bit', 'stdout', chromosome_str, start_str, stop_str ]  )
    return "".join( fix.split('\n')[1:] )

def repair_variant(chromosome, start, stop, ref, alt):
    """
    Repairs incomplete variant report.
    1 - If ref and alt are none, then error, we can't repair this.
    2 - If ref is empty but not alt (insertion), then decrease start position by 1, and prepend base to ref and alt
    3 - If alt is empty but not ref (deletion), the decreate start position by 1, and prepend base to ref and alt.
    4 - Otherwise, don't touch it, but run sanity check anyways.

    """
    start_, ref_, alt_ = start, ref, alt
    empty = [None, '']

    if ref == alt and ref in empty:
        """ The unrepairable case """

        print "Error: ref and alt cannot both be empty..."
        raise ValueError
    elif (ref in empty) or (alt in empty):
        """ The variant needs an anchor. """
        start_ = start - 1
        nc = get_ref(chromosome, start_, start_)
        ref_ = str(nc + ref_).upper()
        alt_ = str(nc + alt_).upper()

    try:
        #Sanity check that variant ref matches reference genome.
        expected = get_ref(chromosome, start_, stop)
        if expected != ref_:
            raise ValueError("Excepted is not the same between reference genome and current variant: " + expected + " v.s. " + ref_)        
    except ValueError as v:
        print v
        print "Something went wrong when trying to match reference variant to reference genome."
        print "chomosome:", chromosome
        print "start:", start_
        print "stop:", stop
        print "ref:", ref_
        print "alt:", alt_
        raise v

    # Everything looks good.
    return chromosome, start_, stop, ref_, alt_

if __name__ == "__main__":
    import sys
    
    args = sys.argv[1:]
    if len(args) < 5:
        print "Usage:"
        print sys.argv[0], "chromosome start stop ref alt"
        sys.exit(-len(args))

    chromosome, start, stop, ref, alt = args

    print "Before:", args
    print "After:", repair_variant(chromosome, int(start), int(stop), ref, alt)
