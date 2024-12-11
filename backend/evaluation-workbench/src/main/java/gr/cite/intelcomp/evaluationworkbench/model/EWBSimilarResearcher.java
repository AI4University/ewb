package gr.cite.intelcomp.evaluationworkbench.model;


public class EWBSimilarResearcher {
    private String id;
    private Double score;
    private String name;
    private Integer nPI;
    private Integer nProjects;
    private Integer nPapers;

    public EWBSimilarResearcher(String id, Double score, String name, Integer nPI, Integer nProjects, Integer nPapers) {
        this.id = id;
        this.score = score;
        this.name = name;
        this.nPI = nPI;
        this.nProjects = nProjects;
        this.nPapers = nPapers;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getnPI() {
        return nPI;
    }

    public void setnPI(Integer nPI) {
        this.nPI = nPI;
    }

    public Integer getnProjects() {
        return nProjects;
    }

    public void setnProjects(Integer nProjects) {
        this.nProjects = nProjects;
    }

    public Integer getnPapers() {
        return nPapers;
    }

    public void setnPapers(Integer nPapers) {
        this.nPapers = nPapers;
    }
}
