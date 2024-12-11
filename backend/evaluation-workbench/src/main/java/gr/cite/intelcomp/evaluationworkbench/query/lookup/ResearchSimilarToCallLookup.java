package gr.cite.intelcomp.evaluationworkbench.query.lookup;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class ResearchSimilarToCallLookup {

    private String id;

    @ParameterName("similarity_method")
    private String similarityMethod;

    @ParameterName("filtering_options")
    private String filteringOptions;

    @ParameterName("ranking_options")
    private String rankingOptions;

    private Long start;

    private Long rows;

    private String year;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSimilarityMethod() {
        return similarityMethod;
    }

    public void setSimilarityMethod(String similarityMethod) {
        this.similarityMethod = similarityMethod;
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

    public String getFilteringOptions() {
        return filteringOptions;
    }

    public void setFilteringOptions(String filteringOptions) {
        this.filteringOptions = filteringOptions;
    }

    public String getRankingOptions() {
        return rankingOptions;
    }

    public void setRankingOptions(String rankingOptions) {
        this.rankingOptions = rankingOptions;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
