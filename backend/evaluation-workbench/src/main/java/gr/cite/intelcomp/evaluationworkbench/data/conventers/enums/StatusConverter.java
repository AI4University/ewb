package gr.cite.intelcomp.evaluationworkbench.data.conventers.enums;

import gr.cite.intelcomp.evaluationworkbench.common.enums.Status;
import jakarta.persistence.Converter;

@Converter
public class StatusConverter extends DatabaseEnumConverter<Status, Short> {
    public Status of(Short i) {
        return Status.of(i);
    }
}
