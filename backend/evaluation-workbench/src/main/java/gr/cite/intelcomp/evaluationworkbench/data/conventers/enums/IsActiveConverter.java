package gr.cite.intelcomp.evaluationworkbench.data.conventers.enums;

import gr.cite.intelcomp.evaluationworkbench.common.enums.IsActive;
import jakarta.persistence.Converter;

@Converter
public class IsActiveConverter extends DatabaseEnumConverter<IsActive, Short> {
    public IsActive of(Short i) {
        return IsActive.of(i);
    }
}
