package gr.cite.intelcomp.evaluationworkbench.query.lookup;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class CallsSimilarToResearcherLookup {

    private String id;

    @ParameterName("similarity_method")
    private String similarityMethod;

    @ParameterName("collection_name")
    private String collectionName;

    private Long start;

    private Long rows;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSimilarityMethod() {
        return similarityMethod;
    }

    public void setSimilarityMethod(String similarityMethod) {
        this.similarityMethod = similarityMethod;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getRows() {
        return rows;
    }

    public void setRows(Long rows) {
        this.rows = rows;
    }
}
