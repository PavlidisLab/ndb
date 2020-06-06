#!/usr/bin/python

class VCFTool(object):
    """
    Assist creation of VCF format files by converting variants to the VCF format.
    """

    def __init__(self, file):
        """
        Load the template and separate boilerplate with actual variant formatting line.
        """
        self.template = []
        self.variant_template = None
        self.ouput = []
        with open(file, 'r') as f:
            for line in f:
                text = line.strip()
                template.append(text)
            self.variant_template = template[-1]
            self.template = template[:-1]
        
        def apply_template_to_variant(self, variant_tuple):
            """
            Takes a template string with formatters "%PLACEHOLDER_NAME" 
            where placeholders for the variants are ordered as a tuple 
            ((CHROMOSOME, POSITION, REF, ALT)).
            """
            output = self.variant_template[:]
            PLACEHOLDERS = ["%"+X for X in "CHROMOSOME,POSITION,REF,ALT".split(",")]
            for x,y in zip(PLACEHOLDERS, variant_tuple):
                output = output.replace(x,y)
            return output                           

        def write_variants(self, variants):
            """
            List of tuples mapped as ((CHROMOSOME, POSITION, REF, ALT))
            """
            self.output = self.variant_template[:]
            for variant in variants:
                text = self.apply_template_to_variant(variant)
                self.output.append(text)

            # Write output
            print "Output"
            print "\n".join(output)
                    
            
                
                
