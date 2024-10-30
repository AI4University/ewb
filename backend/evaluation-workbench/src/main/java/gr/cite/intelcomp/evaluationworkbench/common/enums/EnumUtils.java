package gr.cite.intelcomp.evaluationworkbench.common.enums;

import gr.cite.intelcomp.evaluationworkbench.data.conventers.enums.DatabaseEnum;

import java.util.HashMap;
import java.util.Map;

public class EnumUtils {
    public static  <EnumType extends Enum<EnumType> & DatabaseEnum<EnumValue>, EnumValue> Map<EnumValue, EnumType>  getEnumValueMap(Class<EnumType> enumType){
        HashMap<EnumValue, EnumType> map = new HashMap<>();
        for (EnumType v : enumType.getEnumConstants()) {
            map.put(v.getValue(), v);
        }
        return map;
    }
}
