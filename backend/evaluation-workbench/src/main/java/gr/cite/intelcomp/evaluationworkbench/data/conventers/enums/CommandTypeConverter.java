package gr.cite.intelcomp.evaluationworkbench.data.conventers.enums;

import gr.cite.intelcomp.evaluationworkbench.common.enums.CommandType;
import jakarta.persistence.Converter;

@Converter
public class CommandTypeConverter extends DatabaseEnumConverter<CommandType, Short> {
    public CommandType of(Short i) {
        return CommandType.of(i);
    }
}
