package gr.cite.intelcomp.evaluationworkbench.model.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import gr.cite.intelcomp.evaluationworkbench.model.EWBTopicBeta;

import java.io.IOException;
import java.util.List;

public class EWBBetaDeserializer extends JsonDeserializer<List<EWBTopicBeta>> {
    @Override
    public List<EWBTopicBeta> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String value = p.getValueAsString();
        if (value == null) {
            value = p.readValuesAs(new TypeReference<List<String>>() {
            }).next().get(0);
        }
        return EWBTopicBeta.mapToList(value);
    }
}
