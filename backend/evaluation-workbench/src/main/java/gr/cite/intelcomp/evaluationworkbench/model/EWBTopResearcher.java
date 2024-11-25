package gr.cite.intelcomp.evaluationworkbench.model;

import java.util.Map;

public class EWBTopResearcher {
    private String id;
    private String title;
    private Double topic;
    private Integer words;
    private Integer relevance;
    private Map<String, Integer> counts;

    public EWBTopResearcher(String id) {
        this.id = id;
    }

    public EWBTopResearcher(String id, Double topic, Integer words, Integer relevance, Map<String, Integer> counts) {
        this.id = id;
        this.topic = topic;
        this.words = words;
        this.relevance = relevance;
        this.counts = counts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getTopic() {
        return topic;
    }

    public void setTopic(Double topic) {
        this.topic = topic;
    }

    public Integer getWords() {
        return words;
    }

    public void setWords(Integer words) {
        this.words = words;
    }

    public Integer getRelevance() {
        return relevance;
    }

    public void setRelevance(Integer relevance) {
        this.relevance = relevance;
    }

    public Map<String, Integer> getCounts() {
        return counts;
    }

    public void setCounts(Map<String, Integer> counts) {
        this.counts = counts;
    }
}
