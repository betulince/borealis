package com.betuince.borealis.model;

public class BlogPost extends Content {
    private String type; // "blog" or "book-review"
    private String source; // "local", "substack", "medium"
    private String externalUrl; // URL to the original post if external

    public BlogPost() {
        super();
        this.source = "local"; // default to local
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public boolean isExternal() {
        return !"local".equals(source);
    }
}
