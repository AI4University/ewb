package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class LargeThetasQuery {
    @ParameterName("corpus_collection")
    private String corpusCollection;
    @ParameterName("model_name")
    private String modelName;
    @ParameterName("topic_id")
    private String topicId;
    private Integer threshold;
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

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
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
