package gr.cite.intelcomp.evaluationworkbench.data.conventers.enums;

import gr.cite.intelcomp.evaluationworkbench.common.enums.ScheduledEventType;
import jakarta.persistence.Converter;

@Converter
public class ScheduledEventTypeConverter extends DatabaseEnumConverter<ScheduledEventType, Short> {
    public ScheduledEventType of(Short i) {
        return ScheduledEventType.of(i);
    }
}
