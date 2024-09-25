package gr.cite.intelcomp.evaluationworkbench.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gr.cite.intelcomp.evaluationworkbench.model.deserializer.CommaSeparatorDeserializer;
import gr.cite.intelcomp.evaluationworkbench.model.deserializer.EWBBetaDeserializer;

import java.util.List;

public class EWBTopicMetadata {

    private String id;
    @JsonDeserialize(using = EWBBetaDeserializer.class)
    private List<EWBTopicBeta> betas;
    private float alphas;
    @JsonProperty("topic_entropy")
    private float entropy;
    @JsonProperty("topic_coherence")
    private float coherence;
    @JsonProperty("ndocs_active")
    private Long activeDocs;
    @JsonProperty("tpc_descriptions")
    @JsonDeserialize(using = CommaSeparatorDeserializer.class)
    private List<String> descriptions;
    @JsonProperty("tpc_labels")
    private String label;
    @JsonDeserialize(using = EWBBetaDeserializer.class)
    @JsonProperty("top_words_betas")
    private List<EWBTopicBeta> vocab;
    private List<Double> coords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<EWBTopicBeta> getBetas() {
        return betas;
    }

    public void setBetas(List<EWBTopicBeta> betas) {
        this.betas = betas;
    }

    public float getAlphas() {
        return alphas;
    }

    public void setAlphas(float alphas) {
        this.alphas = alphas;
    }

    public float getEntropy() {
        return entropy;
    }

    public void setEntropy(float entropy) {
        this.entropy = entropy;
    }

    public float getCoherence() {
        return coherence;
    }

    public void setCoherence(float coherence) {
        this.coherence = coherence;
    }

    public Long getActiveDocs() {
        return activeDocs;
    }

    public void setActiveDocs(Long activeDocs) {
        this.activeDocs = activeDocs;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<EWBTopicBeta> getVocab() {
        return vocab;
    }

    public void setVocab(List<EWBTopicBeta> vocab) {
        this.vocab = vocab;
    }

    public List<Double> getCoords() {
        return coords;
    }

    public void setCoords(List<Double> coords) {
        this.coords = coords;
    }
}
