package gr.cite.intelcomp.evaluationworkbench.model.propertynamingstrategy;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class CollectionModelNamingStrategy extends PropertyNamingStrategy {

    @Override
    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return getPrefix(method, defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return getPrefix(method, defaultName);
    }

    private String getPrefix(AnnotatedMethod method, String defaultName) {
        String result = defaultName;
        if (method.hasAnnotation(JsonPrefixName.class)) {
            String prefix = method.getAnnotation(JsonPrefixName.class).value();
            if (defaultName.startsWith(prefix)) {
                result = prefix;
            }
        }
        return result;
    }
}
