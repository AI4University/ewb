package gr.cite.intelcomp.evaluationworkbench.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class EWBDocAG {

    private String id;

    @JsonProperty("SearcheableField")
    private String searchableField;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchableField() {
        return searchableField;
    }

    public void setSearchableField(String searchableField) {
        this.searchableField = searchableField;
    }
}
