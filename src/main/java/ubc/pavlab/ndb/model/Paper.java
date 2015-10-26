package ubc.pavlab.ndb.model;

public final class Paper {
    private final String url;
    private final String name;

    public Paper( String name, String url ) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        if ( this.url == null ) {
            return "#"; // So it doesn't create a borked link.
        }
        return url;
    }
}
