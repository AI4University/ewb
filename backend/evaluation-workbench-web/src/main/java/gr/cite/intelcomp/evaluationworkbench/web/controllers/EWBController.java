package gr.cite.intelcomp.evaluationworkbench.web.controllers;

import gr.cite.intelcomp.evaluationworkbench.model.*;
import gr.cite.intelcomp.evaluationworkbench.query.*;
import gr.cite.intelcomp.evaluationworkbench.query.lookup.*;
import gr.cite.intelcomp.evaluationworkbench.service.ewb.EWBService;
import gr.cite.intelcomp.evaluationworkbench.web.model.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/ewb", produces = MediaType.APPLICATION_JSON_VALUE)
public class EWBController {

    private final EWBService service;

    @Autowired
    public EWBController(EWBService service) {
        this.service = service;
    }

    @GetMapping("listAllCollections")
    public QueryResult<String> listAllCollections() {
        List<String> collections = service.getAllCollections();
        return new QueryResult<>(collections, collections.size());
    }

    @GetMapping("listAllCorpus")
    public QueryResult<String> listAllCorpus() {
        List<String> collections = service.getAllCorpus();
        return new QueryResult<>(collections, collections.size());
    }

    @GetMapping("listAllModels")
    public QueryResult<String> listAllModels() {
        List<String> collections = service.getAllModels();
        return new QueryResult<>(collections, collections.size());
    }

    @GetMapping("listModelsByCorpus")
    public QueryResult<String> listModelsByCorpus(@RequestParam("corpus") String corpus) {
        List<String> collections = service.getModelsForCorpora(corpus);
        return new QueryResult<>(collections, collections.size());
    }

    @PostMapping("listDocsWithHighSemanticRelationship")
    public QueryResult<PrettySemanticsModel> listDocsWithHighSemanticRelationship(@RequestBody SemanticsQuery query) {
        List<PrettySemanticsModel> collections = service.querySemanticsRelationships(query);
        return new QueryResult<>(collections, collections.size());
    }

    @PostMapping("getThetasDoc")
    public QueryResult<EWBPrettyTheta> getThetasDoc(@RequestBody ThetasQuery query) {
        List<EWBPrettyTheta> thetas = service.queryThetas(query);
        return new QueryResult<>(thetas);
    }

    @GetMapping("getNrDocColl")
    public Integer getNrDocColl(@RequestParam("collection") String collection) {
        return service.queryNrDocsColl(collection);
    }

    @GetMapping("listCollectionMetadata")
    public QueryResult<String> listCollectionMetadata(@RequestParam("collection") String collection) {
        List<String> metadata = service.queryCollectionMetadata(collection);
        return new QueryResult<>(metadata);
    }

    @PostMapping("queryDocuments")
    public QueryResult<Map<String, Object>> queryDocs(@RequestBody DocsQuery query) {
        DocsQuery countQuery = new DocsQuery();
        countQuery.setLike(query.getLike());
        countQuery.setCorpusCollection(query.getCorpusCollection());
        long count = service.countQueryDocs(countQuery);
        List<Map<String, Object>> docs = service.queryDocs(query);
        return new QueryResult<>(docs, count);
    }

    @GetMapping("getDocument")
    public Map<String, Object> getDocument(@RequestParam("corpus_collection") String collection, @RequestParam("doc_id") String docId) {
        return service.getDocMetadata(collection, docId);
    }

    @GetMapping("topics")
    public QueryResult<EWBTopicModel> getTopics(@RequestParam("model") String model) {
        List<EWBTopicModel> topics = service.getTopics(model);
        return new QueryResult<>(topics);
    }

    @GetMapping("topics/relations")
    public List<TopicRelation> getTopicRelations(@RequestParam String model) {
        EWBCorrelatedTopicsQuery query = new EWBCorrelatedTopicsQuery();
        query.setModelCollection(model);
        return this.service.getCorrelatedTopics(query);
    }

    @GetMapping("topics/temporal")
    public Map<String, Map<Integer, Double>> getTopicsTemporal(@RequestParam String corpus, @RequestParam String model) {
        return this.service.getTemporalTopics(corpus, model);
    }

    @GetMapping("topics/vocabulary")
    public Map<String, List<EWBTopicBeta>> getTopicVocabulary(@RequestParam String model) {
        return this.service.getTopicsVocabularies(model);
    }

    @PostMapping("similaritiesPairs")
    public QueryResult<EWBSimilarityScore> getSimilarityPair(@RequestBody EWBSimilarityScoreQuery semanticsPairQuery) {
        List<EWBSimilarityScore> similarityScores = this.service.getSimilarityPairs(semanticsPairQuery);
        return new QueryResult<>(similarityScores, similarityScores.size());
    }

    @GetMapping("topics/hierarchical")
    public Map<String, List<EWBTopDoc>> getHierarchicalTopics(@RequestParam String corpus, @RequestParam String model) {
        return this.service.getHierarchicalTopics(corpus, model);
    }

    @PostMapping("topics/topdocs")
    public List<EWBTopDoc> getTopDocuments(@RequestBody TopicTopDocQuery topicTopDocQuery) {
        return this.service.getTopicTopDocs(topicTopDocQuery);
    }

    @GetMapping("topics/metadata")
    public EWBTopicMetadata getTopicMetadata(@RequestParam String model, @RequestParam String topicId) {
        return this.service.getTopicMetadata(model, topicId);
    }

    @GetMapping("topics/allmetadata")
    public List<EWBTopicMetadata> getAllTopicMetadata(@RequestParam String model) {
        return this.service.getAllTopicMetadata(model);
    }

    @GetMapping("topics/topwords")
    public List<EWBTopicBeta> getTopicTopWords(@RequestParam String model, @RequestParam String topicId) {
        return this.service.getTopicsTopWords(model, topicId);
    }

    @PostMapping("listDocsWithHighSimilarityToText")
    public QueryResult<PrettySemanticsModel> listDocsWithHighSimilarityToText(@RequestBody EWBSimilarTextQuery query) {
        List<PrettySemanticsModel> collections = service.listSimilarDocsByText(query);
        return new QueryResult<>(collections, collections.size());
    }

    @GetMapping("list_avail_taxonomies")
    public QueryResult<String> list_avail_taxonomies() {
        List<String> taxonomies = service.list_avail_taxonomies();
        return new QueryResult<>(taxonomies, taxonomies.size());
    }

    @PostMapping("classify")
    public QueryResult<ClassificationResponse> classify(@RequestBody ClassificationQuery query) {
        List<ClassificationResponse> responseList = service.classify(query);
        return new QueryResult<>(responseList, responseList.size());
    }

    @PostMapping("topics/addRelative")
    public boolean addTopic(@RequestBody RelevantTopicModel model) {
        return this.service.addTopic(model);
    }

    @PostMapping("topics/removeRelative")
    public boolean removeTopic(@RequestBody RelevantTopicModel model) {
        return this.service.removeTopic(model);
    }

    @GetMapping("topics/relative")
    public List<EWBTopicMetadata> listUsersTopics(@RequestParam String model) {
        return this.service.getUserTopics(model);
    }

    @PostMapping("topics/isRelevant")
    public Boolean isTopicRelevant(@RequestBody RelevantTopicModel model) {
        return this.service.isTopicRelevant(model);
    }

    @GetMapping("listAllExpertCollections")
    public QueryResult<String> listAllExpertCollections() {
        List<String> collections = service.getAvailableCollections();
        return new QueryResult<>(collections, collections.size());
    }

    @PostMapping("queryExperts")
    public QueryResult<ExpertModel> queryExperts(@RequestBody ExpertQuery query) {
        if (query.getStart() == null) {
            query.setStart(0L);
        }
        List<ExpertModel> expertModels = service.queryExperts(query);
        Long expertCount = service.countExperts(query);
        return new QueryResult<>(expertModels, expertCount);
    }

    @PostMapping("suggestExperts")
    public QueryResult<ExpertModel> suggestExperts(@RequestBody ExpertSuggestionQuery query) {
        List<ExpertModel> expertModels = service.suggestExperts(query);
        return new QueryResult<>(expertModels, expertModels.size());
    }

    @GetMapping("topics/statistics")
    public Map<String, Object> getTopicStatistics(@RequestParam String corpusCollection, @RequestParam String model, @RequestParam String topicId) {
        return this.service.getTopicStatistics(corpusCollection, model, topicId);
    }

    @PostMapping("topics/top-researchers")
    public List<EWBTopResearcher> getTopResearchers(@RequestBody TopicTopResearcherLookup topicTopResearcherLookup) {
        return this.service.getTopicTopResearcher(topicTopResearcherLookup);
    }

    @PostMapping("topics/top-research-groups")
    public List<EWBTopResearchGroup> getTopResearchGroups(@RequestBody TopicTopResearchGroupLookup topicTopResearchGroupLookup) {
        return this.service.getTopicTopResearchGroups(topicTopResearchGroupLookup);
    }

    @GetMapping("topics/evolution")
    public List<Map<String, Object>> getTopicEvolution(@RequestParam String corpusCollection, @RequestParam String model, @RequestParam String topicId) {
        return this.service.getTopicEvolution(corpusCollection, model, topicId);
    }

    @GetMapping("metadata-ag-by-id")
    public List<Map<String, Object>> getMetadataAGByID(@RequestParam String id, @RequestParam String aggregatedCollectionName ) {
        return this.service.getMetadataAGByID(id, aggregatedCollectionName);
    }

    @GetMapping("similarity-criteria-list")
    public List<String> getSimilarityCriteriaList() {
        return this.service.getSimilarityCriteriaList();
    }

    @PostMapping("researchers-similar-to-call")
    public List<EWBSimilarResearcher> getResearchersSimilarToCall(@RequestBody ResearchSimilarToCallLookup lookup) {
        return this.service.getResearchersSimilarToCall(lookup);
    }

    @PostMapping("researchers-similar-to-text")
    public List<EWBSimilarResearcher> getResearchersSimilarToText(@RequestBody ResearchSimilarToTextLookup lookup) {
        return this.service.getResearchersSimilarToText(lookup);
    }

    @PostMapping("research-groups-similar-to-call")
    public List<EWBSimilarResearchGroup> getResearchGroupsSimilarToCall(@RequestBody ResearchSimilarToCallLookup lookup) {
        return this.service.getResearchGroupsSimilarToCall(lookup);
    }

    @PostMapping("research-groups-similar-to-text")
    public List<EWBSimilarResearchGroup> getResearchGroupsSimilarToText(@RequestBody ResearchSimilarToTextLookup lookup) {
        return this.service.getResearchGroupsSimilarToText(lookup);
    }

    @PostMapping("ag-docs-with-string")
    public QueryResult<EWBDocAG> getAGDocsWithString(@RequestBody AGDocsLookup lookup) {
        List<EWBDocAG> docs = this.service.getAGDocsWithString(lookup);
        return new QueryResult<>(docs, docs.size());
    }

    @PostMapping("calls-similar-to-researcher")
    public QueryResult<EWBCallsSimilarToResearcher> getCallsSimilarToResearcher(@RequestBody CallsSimilarToResearcherLookup lookup) {
        List<EWBCallsSimilarToResearcher> results = this.service.getCallsSimilarToResearcher(lookup);
        return new QueryResult<>(results, results.size());
    }

    @PostMapping("thetas-researcher")
    public QueryResult<EWBPrettyTheta> getThetasResearcherByID(@RequestBody ThetasResearcherLookup lookup) {
        List<EWBPrettyTheta> thetas = service.getThetasResearcherByID(lookup);
        return new QueryResult<>(thetas);
    }

}
