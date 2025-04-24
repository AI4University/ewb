package gr.cite.intelcomp.evaluationworkbench.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gr.cite.intelcomp.evaluationworkbench.model.deserializer.EWBThetaDeserializer;

import java.util.List;
import java.util.Map;

public class EWBTopResearchersResponse {

    private String id;

    private String name;

    @JsonDeserialize(using = EWBThetaDeserializer.class)
    private List<EWBTheta> thetas;

    @JsonProperty("num_words_per_researcher")
    private Integer words;

    @JsonProperty("topic_relevance")
    private Integer topicRelevance;

    private Map<String, Integer> counts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EWBTheta> getThetas() {
        return thetas;
    }

    public void setThetas(List<EWBTheta> thetas) {
        this.thetas = thetas;
    }

    public Integer getWords() {
        return words;
    }

    public void setWords(Integer words) {
        this.words = words;
    }

    public Integer getTopicRelevance() {
        return topicRelevance;
    }

    public void setTopicRelevance(Integer topicRelevance) {
        this.topicRelevance = topicRelevance;
    }

    public Map<String, Integer> getCounts() {
        return counts;
    }

    public void setCounts(Map<String, Integer> counts) {
        this.counts = counts;
    }
}
