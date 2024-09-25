package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class RelativeTopicQuery {
    @ParameterName("model_collection")
    private String model;
    private String user;
    private Integer start;
    private Integer rows;

    public RelativeTopicQuery() {
    }

    public RelativeTopicQuery(String model, String user, Integer start, Integer rows) {
        this.model = model;
        this.user = user;
        this.start = start;
        this.rows = rows;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
