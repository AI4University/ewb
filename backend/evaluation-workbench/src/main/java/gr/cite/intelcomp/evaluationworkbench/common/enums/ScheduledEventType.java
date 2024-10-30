package gr.cite.intelcomp.evaluationworkbench.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.intelcomp.evaluationworkbench.data.conventers.enums.DatabaseEnum;

import java.util.Map;

public enum ScheduledEventType implements DatabaseEnum<Short> {
    ;

    private final Short value;

    ScheduledEventType(Short value) {
        this.value = value;
    }

    @JsonValue
    public Short getValue() {
        return value;
    }

    private static final Map<Short, ScheduledEventType> map = EnumUtils.getEnumValueMap(ScheduledEventType.class);

    public static ScheduledEventType of(Short i) {
        return map.get(i);
    }
}
