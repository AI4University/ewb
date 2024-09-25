package gr.cite.intelcomp.evaluationworkbench.query;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class ExpertQuery {

    @ParameterName("expert_collection")
    private String expertCollection;
    @ParameterName("string")
    private String like;
    private Long start;
    private Long rows;

    public String getExpertCollection() {
        return expertCollection;
    }

    public void setExpertCollection(String expertCollection) {
        this.expertCollection = expertCollection;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getRows() {
        return rows;
    }

    public void setRows(Long rows) {
        this.rows = rows;
    }
}
