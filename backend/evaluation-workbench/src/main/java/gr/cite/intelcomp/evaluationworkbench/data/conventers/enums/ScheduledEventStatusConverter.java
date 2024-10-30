package gr.cite.intelcomp.evaluationworkbench.data.conventers.enums;

import gr.cite.intelcomp.evaluationworkbench.common.enums.ScheduledEventStatus;
import jakarta.persistence.Converter;

@Converter
public class ScheduledEventStatusConverter extends DatabaseEnumConverter<ScheduledEventStatus, Short> {
    public ScheduledEventStatus of(Short i) {
        return ScheduledEventStatus.of(i);
    }
}
