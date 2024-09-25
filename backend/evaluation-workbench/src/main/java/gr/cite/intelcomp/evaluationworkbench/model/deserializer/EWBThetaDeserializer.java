package gr.cite.intelcomp.evaluationworkbench.model.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import gr.cite.intelcomp.evaluationworkbench.model.EWBTheta;
import gr.cite.intelcomp.evaluationworkbench.model.EWBTopicBeta;

import java.io.IOException;
import java.util.List;

public class EWBThetaDeserializer extends JsonDeserializer<List<EWBTheta>> {
    @Override
    public List<EWBTheta> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return EWBTheta.mapToPojo(p.getValueAsString());
    }
}
