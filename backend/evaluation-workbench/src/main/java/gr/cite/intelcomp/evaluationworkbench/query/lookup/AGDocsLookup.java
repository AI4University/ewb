package gr.cite.intelcomp.evaluationworkbench.query.lookup;

import gr.cite.intelcomp.evaluationworkbench.webclient.ParameterName;

public class AGDocsLookup {
    @ParameterName("aggregated_collection_name")
    private String aggregatedCollectionName;
    @ParameterName("string")
    private String like;
    private Long start;
    private Long rows;

    public String getAggregatedCollectionName() {
        return aggregatedCollectionName;
    }

    public void setAggregatedCollectionName(String aggregatedCollectionName) {
        this.aggregatedCollectionName = aggregatedCollectionName;
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
