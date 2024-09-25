package gr.cite.intelcomp.evaluationworkbench.common.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Visibility {
    @JsonProperty("Public")
    PUBLIC,
    @JsonProperty("Private")
    PRIVATE();
}
