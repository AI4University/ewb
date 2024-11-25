package gr.cite.intelcomp.evaluationworkbench.query.lookup;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class ThetasResearcherLookup {
    @ParameterName("corpus_collection")
    private String corpusCollection;
    @ParameterName("id")
    private String id;
    @ParameterName("model_name")
    private String modelName;

    public String getCorpusCollection() {
        return corpusCollection;
    }

    public void setCorpusCollection(String corpusCollection) {
        this.corpusCollection = corpusCollection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
