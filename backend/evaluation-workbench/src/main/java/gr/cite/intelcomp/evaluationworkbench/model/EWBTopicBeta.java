package gr.cite.intelcomp.evaluationworkbench.model;

import java.util.ArrayList;
import java.util.List;

public class EWBTopicBeta {

    private String id;
    private Integer beta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getBeta() {
        return beta;
    }

    public void setBeta(Integer beta) {
        this.beta = beta;
    }

    public static List<EWBTopicBeta> mapToList(String betasString) {
        if (betasString.equals("-1")) {
            return null;
        }
        List<EWBTopicBeta> result = new ArrayList<>();
        List<String> topicStrings = List.of(betasString.split(" "));
        for (String topicString : topicStrings) {
            String[] values = topicString.split("\\|");
            EWBTopicBeta topic = new EWBTopicBeta();
            topic.setId(values[0]);
            topic.setBeta((int)Double.parseDouble(values[1]));
            result.add(topic);
        }
        return result;
    }
}
