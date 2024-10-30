package gr.cite.intelcomp.evaluationworkbench.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.intelcomp.evaluationworkbench.data.conventers.enums.DatabaseEnum;

import java.util.Map;

public enum ScheduledEventStatus implements DatabaseEnum<Short> {
	PENDING((short) 0),
	PROCESSING((short) 1),
	SUCCESSFUL((short) 2),
	ERROR((short) 3),
	OMITTED((short) 4),
	PARKED((short) 5),
	DISCARD((short) 6),
	CANCELED((short) 7);

	private final Short value;

	ScheduledEventStatus(Short value) {
		this.value = value;
	}

	@JsonValue
	public Short getValue() {
		return value;
	}

	private static final Map<Short, ScheduledEventStatus> map = EnumUtils.getEnumValueMap(ScheduledEventStatus.class);

	public static ScheduledEventStatus of(Short i) {
		return map.get(i);
	}
}
