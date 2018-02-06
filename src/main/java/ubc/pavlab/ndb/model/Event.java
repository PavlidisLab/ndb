    /*
     * The ndb project
     *
     * Copyright (c) 2015 University of British Columbia
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *       http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     *
     */

    package ubc.pavlab.ndb.model;

    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.Iterator;
    import java.util.List;
    import java.util.Set;

    import com.google.common.collect.HashMultimap;
    import com.google.common.collect.ImmutableList;
    import com.google.common.collect.Lists;
    import com.google.common.collect.Sets;

    import ubc.pavlab.ndb.model.enums.Category;
    import ubc.pavlab.ndb.model.enums.Inheritance;
    import ubc.pavlab.ndb.model.enums.Validation;

    /**
     * TODO Document Me
     *
     * @author mjacobson
     * @version $Id$
     */
    public class Event {

        private final Integer id;
        private final Integer subjectId;
        private final String sampleId;
        private final String chromosome;
        private final Integer start;
        private final Integer stop;
        private final String ref;
        private final String alt;

        private final String lof;
        private final List<Gene> genes;
        private final List<String> funcs;
        private final List<Category> categories;
        private final List<Inheritance> inheritances;
        private final List<Validation> validations;
        private final List<String> validationMethods;

        private final List<AAChange> aaChanges;
        private final List<Variant> variants;

        private final Double caddPhred;
        private final Double exacFreq;

        private final boolean complex;

        public Double getExacFreq() {
            return exacFreq;
        }

        public Event( Collection<Variant> variants ) throws IllegalArgumentException {
            if ( variants == null || variants.isEmpty() ) {
                throw new IllegalArgumentException( "Empty or Null Variants" );
            }

            List<Variant> variantsCopy = new ArrayList<>( variants );
            Collections.sort( variantsCopy, new Comparator<Variant>() {
                @Override
                public int compare( Variant v1, Variant v2 ) {
                    return v1.getPaper().compareTo( v2.getPaper() );

                }
            } );

            Set<Gene> genes = Sets.newHashSet();
            Set<Paper> papers = Sets.newHashSet();
            Set<String> funcs = Sets.newHashSet();
            Set<Category> categories = Sets.newHashSet();
            Set<Inheritance> inheritances = Sets.newHashSet();
            Set<Validation> validations = Sets.newHashSet();
            Set<String> validationMethods = Sets.newHashSet();
            Set<AAChange> aaChanges = Sets.newLinkedHashSet();

            boolean complex = false;
            Variant testVariant = variantsCopy.iterator().next();

            Double caddMax = 0.0;
            Double exacMax = 0.0;

            for ( Variant variant : variantsCopy ) {

                genes.addAll( variant.getGenes() );
                papers.add( variant.getPaper() );
                Double cadd = variant.getAnnovar().getCaddPhred();
                Double exac = variant.getAnnovar().getExac03();

                if ( cadd != null ) {
                    caddMax = Math.max( cadd, caddMax );
                }

                if ( exac != null ) {
                    exacMax = Math.max( exac, exacMax );
                }

                if ( variant.getFunc() != null ) {
                    funcs.add( variant.getFunc() );
                }
                if ( variant.getCategory() != null ) {
                    categories.add( variant.getCategory() );
                }
                if ( variant.getInheritance() != null ) {
                    inheritances.add( variant.getInheritance() );
                }
                if ( variant.getValidation() != null ) {
                    validations.add( variant.getValidation() );
                }
                if ( variant.getValidationMethod() != null ) {
                    validationMethods.add( variant.getValidationMethod() );
                }

                if ( variant.getAaChanges() != null ) {
                    for (String aaChange : variant.getAaChanges()) {
                        if (! aaChange.equals( "UNKNOWN" )){
                            aaChanges.add( new AAChange(aaChange) );
                        }
                    }
                }


                if ( !variant.getEventId().equals( testVariant.getEventId() ) ) {
                    throw new IllegalArgumentException( "Variant list contains multiple event Ids" );
                }

                if ( !variant.getChromosome().equals( testVariant.getChromosome() ) ) {
                    throw new IllegalArgumentException( "Variant list contains multiple Chromosomes" );
                }

                if ( !variant.getSubjectId().equals( testVariant.getSubjectId() ) ) {
                    throw new IllegalArgumentException( "Variant list contains multiple Subjects" );
                }
                // Note: For samples, we could do a check similar to the one right above for subjects, however I can
                // anticipate situations where two distinct Sample IDs are actually the same proband, so that test
                // would not be useful here.

                if ( !complex && !( variant.getStartHg19().equals( testVariant.getStartHg19() )
                        && variant.getStopHg19().equals( testVariant.getStopHg19() )
                        && variant.getRef().equals( testVariant.getRef() )
                        && variant.getAlt().equals( testVariant.getAlt() ) ) ) {
                    complex = true;
                }

            }

            this.id = testVariant.getEventId();

            this.chromosome = testVariant.getChromosome();

            this.subjectId = testVariant.getSubjectId();
            this.sampleId = testVariant.getSampleId();

            if ( complex ) {
                this.start = null;
                this.stop = null;
                this.ref = null;
                this.alt = null;
                this.lof = null;

            } else {
                this.start = testVariant.getStartHg19();
                this.stop = testVariant.getStopHg19();
                this.ref = testVariant.getRef();
                this.alt = testVariant.getAlt();
                this.lof = testVariant.getLoF();
            }

            this.caddPhred = caddMax;
            this.exacFreq = exacMax;

            this.complex = complex;

            this.genes = ImmutableList.copyOf( genes );
            // this.papers = ImmutableList.copyOf( papers );
            this.funcs = ImmutableList.copyOf( funcs );
            this.categories = ImmutableList.copyOf( categories );
            this.inheritances = ImmutableList.copyOf( inheritances );
            this.validations = ImmutableList.copyOf( validations );
            this.validationMethods = ImmutableList.copyOf( validationMethods );
            this.aaChanges = ImmutableList.copyOf( aaChanges );
            this.variants = ImmutableList.copyOf( variantsCopy );
        }

        public Integer getId() {
            return id;
        }

        public Integer getSubjectId() {
            return subjectId;
        }

        public String getSampleId() {
            return sampleId;
        }

        public String getChromosome() {
            return chromosome;
        }

        public Integer getStart() {
            return start;
        }

        public Integer getStop() {
            return stop;
        }

        public String getRef() {
            return ref;
        }

        public String getAlt() {
            return alt;
        }


//        public String getInheritanceText() {
//            StringBuilder result = new StringBuilder();
//            for ( Inheritance i : inheritances ) {
//                result.append( i.getLabel() );
//                result.append( ";" );
//            }
//            return result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "";
//        }
//
//        public String getValidationText() {
//            StringBuilder result = new StringBuilder();
//            for ( Validation v : validations ) {
//                result.append( v.getLabel() );
//                result.append( ";" );
//            }
//            return result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "";
//        }

        public String getLof() {
            return lof;
        }

        public Double getCaddPhred() {
            return caddPhred;
        }

        public List<Gene> getGenes() {
            return genes;
        }

        // public String getGenesString() {
        // StringBuilder sb = new StringBuilder();
        // String delim = "";
        // for ( Gene gene : genes ) {
        // sb.append( delim ).append( gene.getSymbol() );
        // delim = ", ";
        // }
        // return sb.toString();
        // }

        // public List<Paper> getPapers() {
        // return papers;
        // }

        public List<String> getFuncs() {
            return funcs;
        }

        public List<Category> getCategories() {
            return categories;
        }
        public List<Validation> getValidations() {
            return validations;
        }
        public List<String> getValidationMethods() { return validationMethods; }

        public String getValidationMethodsText() {

            StringBuilder result = new StringBuilder();
            for ( Variant v : getVariants() ) {
                if (v.getValidationMethod() == null){
                    continue;
                }
                result.append( v.getValidationMethod() );
                result.append( ";" );
            }
            return result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" ;
        }

        public List<Inheritance> getInheritances() {
            return inheritances;
        }

        public List<AAChange> getAAChanges() { return aaChanges; }
        public List<Variant> getVariants() { return variants; }

        public boolean isComplex() {
            return complex;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
            return result;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            Event other = ( Event ) obj;
            if ( id == null ) {
                if ( other.id != null ) return false;
            } else if ( !id.equals( other.id ) ) return false;
            return true;
        }

        public static List<Event> groupVariants( Collection<Variant> variants ) {
            HashMultimap<Integer, Variant> grouping = HashMultimap.create();

            for ( Variant variant : variants ) {
                grouping.put( variant.getEventId(), variant );
            }

            List<Event> results = Lists.newArrayList();

            for ( Collection<Variant> vSet : grouping.asMap().values() ) {
                results.add( new Event( vSet ) );
            }

            return results;
        }

        public static final Comparator<Event> COMPARE_GENES = new Comparator<Event>() {
            @Override
            public int compare( Event e1, Event e2 ) {

                Iterator<Gene> it1 = e1.getGenes().iterator();
                Iterator<Gene> it2 = e2.getGenes().iterator();

                while ( it1.hasNext() && it2.hasNext() ) {
                    int res = it1.next().compareTo( it2.next() );
                    if ( res != 0 ) {
                        return res;
                    }
                }

                return Integer.compare( e1.getGenes().size(), e2.getGenes().size() );

            }
        };

        public static final Comparator<Event> COMPARE_PAPERS = new Comparator<Event>() {
            @Override
            public int compare( Event e1, Event e2 ) {
                Iterator<Variant> it1 = e1.getVariants().iterator();
                Iterator<Variant> it2 = e2.getVariants().iterator();

                while ( it1.hasNext() && it2.hasNext() ) {
                    int res = it1.next().getPaper().compareTo( it2.next().getPaper() );
                    if ( res != 0 ) {
                        return res;
                    }
                }

                return Integer.compare( e1.getVariants().size(), e2.getVariants().size() );
            }
        };

        public static final Comparator<Event> COMPARE_EFFECTS = new Comparator<Event>() {
            @Override
            public int compare( Event e1, Event e2 ) {
                Iterator<Category> it1 = e1.getCategories().iterator();
                Iterator<Category> it2 = e2.getCategories().iterator();

                while ( it1.hasNext() && it2.hasNext() ) {
                    int a = it1.next().getImpact();
                    int b = it2.next().getImpact();
                    int res = ( a < b ) ? 1 : ( ( a > b ) ? -1 : 0 );
                    if ( res != 0 ) {
                        return res;
                    }
                }

                return Integer.compare( e1.getCategories().size(), e2.getCategories().size() );
            }
        };

        public static final Comparator<Event> COMPARE_LOCATION = new Comparator<Event>() {
            @Override
            public int compare( Event e1, Event e2 ) {
                int i = e1.getChromosome().compareTo( e2.getChromosome() );
                if ( i != 0 ) return i;

                if ( e1.getStart() == null ) {
                    if ( e2.getStart() == null ) {
                        return 0; // equal
                    } else {
                        return -1; // null is before others
                    }
                } else {
                    if ( e2.getStart() == null ) {
                        return 1; // all others are after null
                    } else {
                        return e1.getStart().compareTo( e2.getStart() );
                    }
                }
            }
        };


        public static final Comparator<Event> COMPARE_FUNCS = new Comparator<Event>() {
            @Override
            public int compare( Event e1, Event e2 ) {
                Iterator<String> it1 = e1.getFuncs().iterator();
                Iterator<String> it2 = e2.getFuncs().iterator();

                while ( it1.hasNext() && it2.hasNext() ) {
                    String a = it1.next();
                    String b = it2.next();

                    int res = ( a.compareTo( b ) > 0 ) ? 1 : ( ( b.compareTo( a ) > 0 ) ? -1 : 0 );
                    if ( res != 0 ) {
                        return res;
                    }
                }

                return Integer.compare( e1.getFuncs().size(), e2.getFuncs().size() );
            }
        };


    }
