package ubc.pavlab.ndb.model;

public final class Paper {
    private final String author;

    public String getName() {
        return author;
    }

    public Paper( String author ) {
        this.author = author;
    }
}
