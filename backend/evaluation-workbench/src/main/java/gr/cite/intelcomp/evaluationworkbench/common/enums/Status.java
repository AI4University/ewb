package gr.cite.intelcomp.evaluationworkbench.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.intelcomp.evaluationworkbench.data.conventers.enums.DatabaseEnum;

import java.util.Map;

public enum Status implements DatabaseEnum<Short> {
    NEW((short) 0),
    RUNNING((short) 1),
    FINISHED((short) 2),
    ERROR((short) 3);

    private final Short value;

    Status(Short value) {
        this.value = value;
    }

    @JsonValue
    public Short getValue() {
        return value;
    }

    private static final Map<Short, Status> map = EnumUtils.getEnumValueMap(Status.class);

    public static Status of(Short i) {
        return map.get(i);
    }
}
