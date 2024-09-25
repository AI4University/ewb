package gr.cite.intelcomp.evaluationworkbench.model;

import java.util.ArrayList;
import java.util.List;

public class EWBTheta {
    private String id;
    private double theta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public static List<EWBTheta> mapToPojo(String doctpc) {
        if (doctpc.equals("-1")) {
            return null;
        }
        List<EWBTheta> result = new ArrayList<>();
        List<String> topicStrings = List.of(doctpc.split(" "));
        for (String topicString : topicStrings) {
            String[] values = topicString.split("\\|");
            EWBTheta topic = new EWBTheta();
            topic.setId(values[0]);
            topic.setTheta(Double.parseDouble(values[1]));
            result.add(topic);
        }
        return result;
    }
}
