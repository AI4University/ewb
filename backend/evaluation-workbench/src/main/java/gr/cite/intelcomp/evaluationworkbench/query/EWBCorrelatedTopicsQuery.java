package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class EWBCorrelatedTopicsQuery {
    @ParameterName("model_collection")
    private String modelCollection;
    @ParameterName("topic_id")
    private Integer topicId;
    private Integer start;
    private Integer rows;

    public String getModelCollection() {
        return modelCollection;
    }

    public void setModelCollection(String modelCollection) {
        this.modelCollection = modelCollection;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
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
