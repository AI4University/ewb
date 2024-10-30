package gr.cite.intelcomp.evaluationworkbench.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.intelcomp.evaluationworkbench.data.conventers.enums.DatabaseEnum;

import java.util.Map;

//TODO: package polution
public enum IsActive implements DatabaseEnum<Short> {
	INACTIVE((short) 0),
	ACTIVE((short) 1);

	private final Short value;

	IsActive(Short value) {
		this.value = value;
	}

	@JsonValue
	public Short getValue() {
		return value;
	}

	private static final Map<Short, IsActive> map = EnumUtils.getEnumValueMap(IsActive.class);

	public static IsActive of(Short i) {
		return map.get(i);
	}
}
