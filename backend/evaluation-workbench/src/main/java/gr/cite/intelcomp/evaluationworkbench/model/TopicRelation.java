package gr.cite.intelcomp.evaluationworkbench.model;

import java.util.List;

public class TopicRelation {

    private String id;
    private List<EWBScore> correlations;

    public TopicRelation(String id, List<EWBScore> correlations) {
        this.id = id;
        this.correlations = correlations;
    }

    public TopicRelation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<EWBScore> getCorrelations() {
        return correlations;
    }

    public void setCorrelations(List<EWBScore> correlations) {
        this.correlations = correlations;
    }
}
