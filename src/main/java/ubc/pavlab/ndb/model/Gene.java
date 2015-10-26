package ubc.pavlab.ndb.model;

public final class Gene {
    private final String name;
    private final String ensembl_id;

    public String getName() {
        return name;
    }

    public Gene( String name, String ensembl_id ) {
        this.name = name;
        this.ensembl_id = ensembl_id;
    }

    public String getEnsembl_id() {
        return ensembl_id;
    }
}
