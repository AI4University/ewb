package gr.cite.intelcomp.evaluationworkbench.model;

public class EWBPrettyTheta {
    private String id;
    private String name;
    private Double theta;

    public EWBPrettyTheta() {
    }

    public EWBPrettyTheta(String id, String name, Double theta) {
        this.id = id;
        this.name = name;
        this.theta = theta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTheta() {
        return theta;
    }

    public void setTheta(Double theta) {
        this.theta = theta;
    }
}
