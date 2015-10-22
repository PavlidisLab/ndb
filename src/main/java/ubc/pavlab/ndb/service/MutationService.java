package ubc.pavlab.ndb.service;

import java.util.ArrayList;
import java.util.List;

import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Mutation;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.model.SourceMutation;

public class MutationService {

    public List<Mutation> createMutations() {
        List<Mutation> list = new ArrayList<Mutation>();

        for ( int i = 0; i < 50; i++ ) {
            String idx = Integer.toString( i / 5 );
            list.add( new Mutation( i++, new Paper( "paper" + idx ), new Gene( "gene1" + idx ), "sampleIdentifier",
                    "chromosome12" + idx, 12837123 + i, 12837128 + i, "AAT", "-", "Missense", "GarbleGableCodeChange",
                    "garblegarbleprotchange", "0.9", "No idea", "Maybe", true ) );

            idx = Integer.toString( i );
            list.add( new Mutation( i++, new Paper( "paper" + idx ), new Gene( "gene1" + idx ), "sampleIdentifier",
                    "chromosome12" + idx, 12837 + i, 12837 + i, "CA", "G", "Missense", "GarbleGableCodeChange",
                    "garblegarbleprotchange", "0.4", "No idea", "Maybe", true ) );

            idx = Integer.toString( i );
            list.add( new Mutation( i++, new Paper( "paper" + idx ), new Gene( "gene1" + idx ), "sampleIdentifier",
                    "chromosome12" + idx, 12837123, 12837128, "C", "GAA", "Missense", "GarbleGableCodeChange",
                    "garblegarbleprotchange", "0.3", "No idea", "Maybe", true ) );

            idx = Integer.toString( i );
            list.add( new Mutation( i++, new Paper( "paper" + idx ), new Gene( "gene1" + idx ), "sampleIdentifier",
                    "chromosome12" + idx, 35234634, 334534636, "-", "G", "Missense", "GarbleGableCodeChange",
                    "garblegarbleprotchange", "0.1", "No idea", "Maybe", true ) );

            idx = Integer.toString( i );
            list.add( new Mutation( i++, new Paper( "paper" + idx ), new Gene( "gene1" + idx ), "sampleIdentifier",
                    "chromosome12" + idx, 123233 + i, 123233 + i, "C", "G", "Missense", "GarbleGableCodeChange",
                    "garblegarbleprotchange", "0.9", "No idea", "Maybe", true ) );
        }
        return list;
    }

    public List<Mutation> getRawMutations( int idx ) {
        /*
         * Get list of mutations tied to an ALPHA mutation
         */

        List<Mutation> list = new ArrayList<Mutation>();

        // TODO: Normally, idx would be used to fetch a specific set of raw variants
        if ( idx == 6 ) {
            for ( int i = 0; i < 10; i++ ) {

                list.add( new Mutation( i, new Paper( "paper" + i ), new Gene( "gene1" + idx ), "sampleIdentifier",
                        "chromosome12", 12837123 + i, 12837128 + i, "AAT", "-", "Missense", "GarbleGableCodeChange",
                        "garblegarbleprotchange", "0.9", "No idea", "Maybe", true ) );
            }
        }
        return list;
    }

    public Mutation getTrunkMutation( int idx ) {
        Mutation trunk = null;
        // TODO: Return appropriate trunk mutation
        if ( idx == 6 ) {
            trunk = new Mutation( idx, new Paper( "paper" + idx ), new Gene( "gene1" + idx ), "sampleIdentifier",
                    "chromosome12", 12837123 + idx, 12837128 + idx, "AAT", "-", "Missense", "GarbleGableCodeChange",
                    "garblegarbleprotchange", "0.9", "No idea", "Maybe", true );
        }

        return trunk;
    }

    public SourceMutation getSourceMutation() { // String idx ) {
        /*
         * Return the key-value pairs stored as metadata for the raw variants.
         */
        SourceMutation raw = null;
        // TODO: Return appropriate metadata for variant id
        // ( Integer.parseInt( idx ) == 0 ) {
        // Hashtable<String, String> kv = new Hashtable<String, String>();
        String idx = "0";
        raw = new SourceMutation( Integer.parseInt( idx ), new Paper( "paper" + idx ), null );
        // }

        return raw;

    }

}
