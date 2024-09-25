package gr.cite.intelcomp.evaluationworkbench.model;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class EWBRelevanceTopicModel {
    @ParameterName("model_name")
    private String model;
    private String user;
    @ParameterName("topic_id")
    private Integer topicId;

    public EWBRelevanceTopicModel() {
    }

    public EWBRelevanceTopicModel(String model, String user, Integer topicId) {
        this.model = model;
        this.user = user;
        this.topicId = topicId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }
}
