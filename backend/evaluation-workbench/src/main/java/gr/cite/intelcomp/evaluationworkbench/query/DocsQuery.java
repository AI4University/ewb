package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class DocsQuery {
    @ParameterName("corpus_collection")
    private String corpusCollection;
    @ParameterName("string")
    private String like;
    private Long start;
    private Long rows;

    public String getCorpusCollection() {
        return corpusCollection;
    }

    public void setCorpusCollection(String corpusCollection) {
        this.corpusCollection = corpusCollection;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
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
