package gr.cite.intelcomp.evaluationworkbench.model;

import java.util.List;
import java.util.Map;

public class ClassificationModel {

    public static List<ClassificationResponse> getResponseModel(Map<String, List<Object>> model) {
        return model.values().stream().map(val -> {
            ClassificationResponse response = new ClassificationResponse();
            if (val.get(1) == null)
                response.setType(val.get(0).toString());
            else
                response.setType(val.get(1).toString());
            response.setValue(Float.valueOf(val.get(2).toString()));
            return response;
        }).toList();
    }

}
