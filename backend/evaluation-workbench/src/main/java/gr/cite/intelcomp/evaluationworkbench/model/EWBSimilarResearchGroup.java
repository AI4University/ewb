package gr.cite.intelcomp.evaluationworkbench.model;


public class EWBSimilarResearchGroup {
    private String id;
    private Double score;
    private String name;
    private Integer nProjects;
    private Integer nPapers;

    public EWBSimilarResearchGroup(String id, Double score, String name, Integer nProjects, Integer nPapers) {
        this.id = id;
        this.score = score;
        this.name = name;
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
