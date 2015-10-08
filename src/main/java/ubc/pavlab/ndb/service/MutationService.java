package ubc.pavlab.ndb.service;

import java.util.ArrayList;
import java.util.List;

import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Mutation;
import ubc.pavlab.ndb.model.Paper;

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
}
