package gr.cite.intelcomp.evaluationworkbench.data.conventers.enums;

import gr.cite.intelcomp.evaluationworkbench.common.enums.ContactInfoType;
import jakarta.persistence.Converter;

@Converter
public class ContactInfoTypeConverter extends DatabaseEnumConverter<ContactInfoType, Short>{
    protected ContactInfoType of(Short i) {
        return ContactInfoType.of(i);
    }
}
