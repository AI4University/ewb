package gr.cite.intelcomp.evaluationworkbench.model;

import java.util.ArrayList;
import java.util.List;

public class SemanticsModel {
    private String id;
    private Double score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public static List<SemanticsModel> generateModels(String text) {
        List<SemanticsModel> result = new ArrayList<>();
        List<String> models = List.of(text.split(" "));
        for (String modelText: models) {
            String[] values = modelText.split("\\|");
            SemanticsModel model = new SemanticsModel();
            model.setId(values[0]);
            model.setScore(Double.parseDouble(values[1]) * 100.0);
            result.add(model);
        }
        return result;
    }
}
