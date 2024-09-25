package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class EWBThetaAndDateQuery {

    @ParameterName("corpus_collection")
    private String corpusCollection;
    @ParameterName("model_name")
    private String modelName;
    private Integer start;
    private Integer rows;

    public String getCorpusCollection() {
        return corpusCollection;
    }

    public void setCorpusCollection(String corpusCollection) {
        this.corpusCollection = corpusCollection;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
