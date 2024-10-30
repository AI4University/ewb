package gr.cite.intelcomp.evaluationworkbench.data.conventers.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public interface DatabaseEnum<T> {
    @JsonValue
    T getValue();
}
