package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class ExpertSuggestionQuery {
    private String text;

    @ParameterName("expert_collection")
    private String expertCollection;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExpertCollection() {
        return expertCollection;
    }

    public void setExpertCollection(String expertCollection) {
        this.expertCollection = expertCollection;
    }
}
