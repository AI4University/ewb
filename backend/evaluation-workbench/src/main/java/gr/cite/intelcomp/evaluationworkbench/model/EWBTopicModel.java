package gr.cite.intelcomp.evaluationworkbench.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class EWBTopicModel {
    private String id;
    private String tpcLabels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonGetter("tpc_labels")
    public String getTpcLabels() {
        return tpcLabels;
    }

    @JsonSetter("tpc_labels")
    public void setTpcLabels(String tpcLabels) {
        this.tpcLabels = tpcLabels;
    }
}
