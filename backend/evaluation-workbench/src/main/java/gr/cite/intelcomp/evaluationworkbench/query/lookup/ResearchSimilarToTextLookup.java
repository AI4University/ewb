package gr.cite.intelcomp.evaluationworkbench.query.lookup;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class ResearchSimilarToTextLookup {

    private String text;

    @ParameterName("similarity_method")
    private String similarityMethod;

    private Long start;

    private Long rows;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSimilarityMethod() {
        return similarityMethod;
    }

    public void setSimilarityMethod(String similarityMethod) {
        this.similarityMethod = similarityMethod;
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
