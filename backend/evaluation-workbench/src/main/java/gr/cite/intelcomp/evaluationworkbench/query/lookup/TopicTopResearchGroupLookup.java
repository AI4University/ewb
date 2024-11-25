package gr.cite.intelcomp.evaluationworkbench.query.lookup;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class TopicTopResearchGroupLookup {
    @ParameterName("corpus_collection")
    private String corpusCollection;
    @ParameterName("model_name")
    private String modelName;
    @ParameterName("topic_id")
    private Integer topicId;
    private Long start;
    private Long rows;

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

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
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
