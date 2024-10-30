package gr.cite.intelcomp.evaluationworkbench.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.intelcomp.evaluationworkbench.data.conventers.enums.DatabaseEnum;

import java.util.Map;

public enum CommandType implements DatabaseEnum<Short> {
    WORDLIST_GET ((short) 0),WORDLIST_CREATE ((short) 1), WORDLIST_COPY ((short) 2), WORDLIST_RENAME ((short) 3), WORDLIST_DELETE ((short) 4),
    CORPUS_GET ((short) 5), CORPUS_GET_RAW ((short) 6), CORPUS_GET_LOGICAL ((short) 7), CORPUS_CREATE ((short) 9), CORPUS_COPY ((short) 9),
    CORPUS_RENAME ((short) 10), CORPUS_DELETE ((short) 11),
    MODEL_GET ((short) 12), MODEL_GET_TOPIC ((short) 13), MODEL_GET_DOMAIN ((short) 14), MODEL_COPY ((short) 15), MODEL_RENAME ((short) 16), MODEL_DELETE ((short) 17),
    MODEL_RESET ((short) 18), TOPIC_GET((short) 19), TOPIC_LABELS_SET((short) 20), TOPIC_SIMILAR((short) 21), TOPIC_FUSE((short) 22), TOPIC_SORT((short) 23),
    TOPIC_DELETE ((short) 24)
    ;

    private final Short value;

    CommandType(Short value) {
        this.value = value;
    }

    @JsonValue
    public Short getValue() {
        return value;
    }

    private static final Map<Short, CommandType> map = EnumUtils.getEnumValueMap(CommandType.class);

    public static CommandType of(Short i) {
        return map.get(i);
    }
}
