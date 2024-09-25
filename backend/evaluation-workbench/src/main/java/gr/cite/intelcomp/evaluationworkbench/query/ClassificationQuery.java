package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class ClassificationQuery {

    private String text;

    @ParameterName("taxonmy")
    private String taxonomy;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

}
