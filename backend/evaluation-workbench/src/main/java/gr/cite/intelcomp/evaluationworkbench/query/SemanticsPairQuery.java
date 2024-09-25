package gr.cite.intelcomp.evaluationworkbench.query;

public class SemanticsPairQuery {
    private String corpus;
    private String model;
    private Double lowerPercent;
    private Double higherPercent;
    private Integer year;
    private Integer start;
    private Integer rows;

    public String getCorpus() {
        return corpus;
    }

    public void setCorpus(String corpus) {
        this.corpus = corpus;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getLowerPercent() {
        return lowerPercent;
    }

    public void setLowerPercent(Double lowerPercent) {
        this.lowerPercent = lowerPercent;
    }

    public Double getHigherPercent() {
        return higherPercent;
    }

    public void setHigherPercent(Double higherPercent) {
        this.higherPercent = higherPercent;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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
