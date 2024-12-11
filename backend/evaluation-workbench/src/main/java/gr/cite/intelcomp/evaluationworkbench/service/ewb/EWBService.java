package gr.cite.intelcomp.evaluationworkbench.service.ewb;

import gr.cite.intelcomp.evaluationworkbench.model.*;
import gr.cite.intelcomp.evaluationworkbench.query.*;
import gr.cite.intelcomp.evaluationworkbench.query.lookup.*;
import gr.cite.intelcomp.evaluationworkbench.webclient.WebClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EWBService {

    private final WebClient ewbTMClient;

    private final WebClient ewbClassificationClient;

    //TODO: This will be deleted
    private static final Map<String, List<ExpertModel>> experts = Map.of("test1", List.of(new ExpertModel(
            "1",
            "test1",
            "test1",
            "test1@test1.test1",
            "test1",
            "test1",
            "test1",
            "test1",
            "test1",
            "test1",
            "test1",
            List.of("test1"))),
            "test2", List.of(new ExpertModel(
                    "1",
                    "test2",
                    "test2",
                    "test2@test2.test2",
                    "test2",
                    "test2",
                    "test2",
                    "test2",
                    "test2",
                    "test2",
                    "test2",
                    List.of("test2")
            )));

    @Autowired
    public EWBService(@Qualifier("ewbTMClient") WebClient ewbTMClient, @Qualifier("ewbClassificationClient") WebClient ewbClassificationClient) {
        this.ewbTMClient = ewbTMClient;
        this.ewbClassificationClient = ewbClassificationClient;
    }

    public List<String> getAllCollections() {
        return ewbTMClient.get().uri("/collections/listCollections").accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })).block();
    }

    public List<String> getAllCorpus() {
        return ewbTMClient.get().uri("/corpora/listAllCorpus")
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })).block();
    }

    public List<String> getModelsForCorpora(String corpora) {
        return ewbTMClient.get().uri("/corpora/listCorpusModels", builder -> builder.queryParam("corpus_col", corpora).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })).block();
    }

    public List<String> getAllModels() {
        return ewbTMClient.get().uri("/models/listAllModels")
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })).block();
    }

    public List<Map<String, Object>> queryCollection(CollectionQuery query) {
        return ewbTMClient.get().uri("/collections/query", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })).block();
    }

    public List<PrettySemanticsModel> querySemanticsRelationships(SemanticsQuery query) {
        List<PrettySemanticsModel> result;
        List<SemanticsModel> semanticsModels = this.querySemanticsRelationshipsInternal(query);
        CollectionQuery collectionQuery = new CollectionQuery();
        collectionQuery.setCollection(query.getCorpusCollection());
        collectionQuery.setQ("id: (" + semanticsModels.stream().map(SemanticsModel::getId).collect(Collectors.joining(" ")) + ")");
        collectionQuery.setQop("OR");
        collectionQuery.setFl("id, title");
        collectionQuery.setRows(query.getRows());
        List<Map<String, Object>> collectedData = this.queryCollection(collectionQuery);
        result = semanticsModels.stream().sorted(Comparator.comparingDouble(SemanticsModel::getScore).reversed()).filter(semanticsModel -> !semanticsModel.getId().equals(query.getDocId())).map(semanticsModel -> {
            PrettySemanticsModel prettySemanticsModel = new PrettySemanticsModel();
            Map<String, Object> doc = collectedData.stream().filter(docl -> docl.get("id").toString().equals(semanticsModel.getId())).findFirst().orElse(new HashMap<>());
            if (!doc.isEmpty()) {
                prettySemanticsModel.setId(doc.get("id").toString());
                prettySemanticsModel.setTitle(doc.get("title").toString());
                prettySemanticsModel.setScore(semanticsModel.getScore());
                return prettySemanticsModel;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return result;
    }

    private List<SemanticsModel> querySemanticsRelationshipsInternal(SemanticsQuery query) {
        return ewbTMClient.get().uri("/queries/getDocsWithHighSimWithDocByid", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, query))
                .exchangeToMono(mono -> {
                    if (mono.statusCode() != HttpStatus.OK) {
                        return Mono.empty();
                    }
                    return mono.bodyToMono(new ParameterizedTypeReference<List<SemanticsModel>>() {
                    });
                }).block();
    }

    public List<Map<String, Object>> queryLargeThetas(LargeThetasQuery query) {
        return ewbTMClient.get().uri("/queries/getDocsWithThetasLargerThanThr", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })).block();
    }

    public Integer queryNrDocsColl(String collection) {
        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getNrDocsColl", builder -> builder.queryParam("collection", collection).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<Map<String, Integer>>() {
                })).block()).entrySet().stream().findFirst().orElse(Map.entry("", -1)).getValue();
    }

    public List<EWBPrettyTheta> queryThetas(ThetasQuery query) {
        String response = Objects.requireNonNull(ewbTMClient.get().uri("/queries/getThetasDocById", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })).block()).entrySet().stream().findFirst().orElse(Map.entry("", "")).getValue();
        return this.buildThetas(response, query.getModelName());
    }

    public List<String> queryCollectionMetadata(String corpus) {
        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getCorpusMetadataFields", builder -> builder.queryParam("corpus_collection", corpus).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<Map<String, List<String>>>() {
                })).block()).entrySet().stream().findFirst().orElse(Map.entry("", List.of())).getValue();
    }

    public List<String> queryDocsInternal(DocsQuery query) {
        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getDocsWithString", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, String>>>() {
                })).block()).stream().map(stringStringMap -> stringStringMap.entrySet().stream().findFirst().orElse(Map.entry("", "")).getValue()).collect(Collectors.toList());
    }

    public long countQueryDocs(DocsQuery query) {
        return queryDocsInternal(query).size();
    }

    public List<Map<String, Object>> queryDocs(DocsQuery query) {
        List<String> docIds = queryDocsInternal(query);
        return docIds.stream().map(docId -> {
            Map<String, Object> value = this.getDocMetadata(query.getCorpusCollection(), docId);
            value.forEach((key, value1) -> {
                if (value1 instanceof String) {
                    try {
                        Instant timeValue = Instant.parse((String) value1);
                        value.put(key, timeValue);
                    } catch (DateTimeParseException e) {
                        value.put(key, value1);
                    }
                }
            });
            return value;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getDocMetadata(String corpus, String docId) {
        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getMetadataDocById", builder ->
                        builder.queryParam("corpus_collection", corpus).queryParam("doc_id", docId).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })).block()).stream().peek(doc -> {
            String simKey = doc.keySet().stream().filter(key -> key.startsWith("sim_")).findFirst().orElse("");
            doc.remove(simKey);
            String ndocsKey = doc.keySet().stream().filter(key -> key.equals("nwords_per_doc")).findFirst().orElse("");
            doc.remove(ndocsKey);
            if (!doc.containsKey("id")) {
                doc.put("id", docId);
            }
        }).findFirst().orElse(null);
    }

    public List<EWBTopicModel> getTopics(String model) {
        return ewbTMClient.get().uri("/queries/getTopicsLabels", builder -> builder.queryParam("model_collection", model).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopicModel>>() {
                })).block();
    }

    /**
     * Due to the size of the data, keep it static to 10 rows per requests
     */
    private static final Integer THETA_MAX_ROWS = 100;

    public Map<String, Map<String, Double>> getThetaRelations(LargeThetasQuery largeThetasQuery) {
        Map<String, Map<String, Double>> relationThetas = new HashMap<>();
        int maxTopics = this.queryNrDocsColl(largeThetasQuery.getModelName());
        largeThetasQuery.setRows(THETA_MAX_ROWS);
        int index;
        List<String> blackListedIds = new ArrayList<>();
        for (int i = 0; i < maxTopics; i++) {
            index = 0;
            largeThetasQuery.setStart(index);
            relationThetas.put("t" + i, new HashMap<>());
            largeThetasQuery.setTopicId(Integer.toString(i));
            List<Map<String, Object>> docs = this.queryLargeThetas(largeThetasQuery);
            docs = docs.stream().filter(doc -> !blackListedIds.contains(doc.get("id").toString())).collect(Collectors.toList());
            do {
                int finalI = i;
                docs.forEach(doc -> {
                    blackListedIds.add(doc.get("id").toString());
                    String topicString = (String) doc.get("doctpc_" + largeThetasQuery.getModelName());
                    List<String> topics = List.of(topicString.split(" "));
                    topics.forEach(topic -> {
                        String[] values = topic.split("\\|");
                        if (!relationThetas.get("t" + finalI).containsKey(values[0])) {
                            relationThetas.get("t" + finalI).put(values[0], Double.parseDouble(values[1]));
                        } else {
                            relationThetas.get("t" + finalI).put(values[0], relationThetas.get("t" + finalI).get(values[0]) + Double.parseDouble(values[1]));
                        }
                    });

                });
                index += THETA_MAX_ROWS;
                largeThetasQuery.setStart(index);
                docs = this.queryLargeThetas(largeThetasQuery);
            } while (docs.size() == THETA_MAX_ROWS);
        }
        return relationThetas;
    }

    private static final Integer MAX_TEMPORAL_STEP = 1000;

    public Map<String, Map<Integer, Double>> getTemporalTopics(String corpus, String model) {
        Map<String, Map<Integer, Double>> result = new TreeMap<>();
        List<Map<String, Object>> docs = new ArrayList<>();
        int index = 0;
        int minYear = 10000;
        int maxYear = 0;
        EWBThetaAndDateQuery query = new EWBThetaAndDateQuery();
        query.setCorpusCollection(corpus);
        query.setModelName(model);
        query.setStart(index);
        query.setRows(MAX_TEMPORAL_STEP);
        String topicField = "doctpc_" + model;
        while (true) {
            query.setStart(index);
            List<Map<String, String>> tdocs = this.getThetasAndDates(query);
            if (tdocs.isEmpty()) {
                break;
            }
            List<Map<String, Object>> parsedDocs = tdocs.stream().map(doc -> {
                String doctpcKey = doc.keySet().stream().filter(key -> key.startsWith("doctpc")).findFirst().orElse("");
                String doctpcValue = doc.remove(doctpcKey);
                Map<String, Object> newDoc = new HashMap<>(doc);
                newDoc.put(doctpcKey, EWBTheta.mapToPojo(doctpcValue));
                return newDoc;
            }).toList();
            docs.addAll(parsedDocs);
            index += MAX_TEMPORAL_STEP;
        }
        for (Map<String, Object> doc : docs) {
            if (doc.containsKey(topicField)) {
                @SuppressWarnings("unchecked")
                List<EWBTheta> topics = (List<EWBTheta>) doc.get(topicField);
                if (topics != null) {
                    for (EWBTheta topic : topics) {
                        if (!result.containsKey(topic.getId())) {
                            result.put(topic.getId(), new TreeMap<>());
                        }
                        if (doc.containsKey("date") && doc.get("date") != null) {
                            Integer year = Integer.parseInt(doc.get("date").toString().split("-")[0]);
                            if (year < minYear) {
                                minYear = year;
                            }
                            if (year > maxYear) {
                                maxYear = year;
                            }
                            if (!result.get(topic.getId()).containsKey(year)) {
                                result.get(topic.getId()).put(year, topic.getTheta());
                            } else {
                                result.get(topic.getId()).put(year, result.get(topic.getId()).get(year) + topic.getTheta());
                            }
                        }
                    }
                }
            }
        }
        int finalMinYear = minYear;
        int finalMaxYear = maxYear;
        /*Double maxValue = result.entrySet().stream().map(stringMapEntry -> stringMapEntry.getValue().values()).max(Comparator.comparing(o -> o.stream().max(Double::compareTo).orElse(0.0))).get().stream().max(Double::compareTo).orElse(0.0);
        Double divider = 10.0;
        while(true) {
            if (maxValue / divider <= 0.1) {
                break;
            }
            divider += 10.0;
        }
        Double finalDivider = divider;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);*/
        result.forEach((key, value) -> {
            for (int i = finalMinYear; i < finalMaxYear + 1; i++) {
                if (!value.containsKey(i)) {
                    value.put(i, 0.0);
                } else {
                    value.put(i, value.get(i) / 1000.0d);
                }/* else {
                    value.put(i, Double.parseDouble(decimalFormat.format(value.get(i) / finalDivider)));
                }*/
            }
            result.put(key, value);
        });
        return result;
    }

    private List<Map<String, String>> getThetasAndDates(EWBThetaAndDateQuery query) {
        return this.ewbTMClient.get().uri("/queries/getThetasAndDateAllDocs", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, String>>>() {
                })).block();
    }

    public Map<String, List<EWBTopDoc>> getHierarchicalTopics(String corpus, String model) {
        TopicTopDocQuery topicTopDocQuery = new TopicTopDocQuery();
        topicTopDocQuery.setCorpusCollection(corpus);
        topicTopDocQuery.setModelName(model);
        topicTopDocQuery.setRows(10L);
        Map<String, List<EWBTopDoc>> result = new HashMap<>();
        Integer count = this.queryNrDocsColl(model);
        for (int i = 0; i < count; i++) {
            topicTopDocQuery.setTopicId(i);
            List<EWBTopDoc> docs = this.getTopicTopDocs(topicTopDocQuery);
            result.put("t" + i, docs);
            /*final String topicId = "t" + i;
            result.put(topicId, docs.stream().map(doc -> {
                List<EWBTheta> thetas = EWBTheta.mapToPojo(doc.get("doctpc_" + model).toString());
                EWBTheta tres = null;
                if (thetas != null) {
                    tres = thetas.stream()
                            .filter(theta -> theta.getId().equals(topicId))
                            .peek(theta -> theta.setId(doc.get("id").toString())).findFirst().orElse(null);
                    if (tres != null)
                        tres.setTheta(tres.getTheta() * Integer.parseInt(doc.get("nwords_per_doc").toString()));
                }
                return tres;
            }).limit(10).collect(Collectors.toList()));*/
        }
        return result;
    }

    public List<EWBTopDoc> getTopicTopDocs(TopicTopDocQuery query) {
        List<EWBTopDoc> results = Objects.requireNonNull(ewbTMClient.get().uri("/queries/getTopicTopDocs/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopDocResponse>>() {
                })).block()).stream().map(doc -> new EWBTopDoc(doc.getId(), doc.getThetas() != null ? doc.getThetas().get(0).getTheta() : 0, doc.getWords(), doc.getTopicRelevance(), doc.getCounts())).collect(Collectors.toList());
        CollectionQuery collectionQuery = new CollectionQuery();
        collectionQuery.setCollection(query.getCorpusCollection());
        collectionQuery.setQ("id: (" + results.stream().map(EWBTopDoc::getId).collect(Collectors.joining(" ")) + ")");
        collectionQuery.setQop("OR");
        collectionQuery.setFl("id, title");
        collectionQuery.setRows(results.size());
        List<Map<String, Object>> collectedData = this.queryCollection(collectionQuery);
        results.forEach(doc -> doc.setTitle(collectedData.stream().filter(cd -> cd.get("id").equals(doc.getId())).map(cd -> cd.get("title").toString()).findFirst().orElse("N/A")));
        results.sort(Comparator.comparing((EWBTopDoc topDoc) -> topDoc.getTopic() * topDoc.getRelevance()).reversed());
        return results;
    }

    public Map<String, List<EWBTopicBeta>> getTopicsVocabularies(String model) {
        Map<String, List<EWBTopicBeta>> result = new HashMap<>();
        Integer count = this.queryNrDocsColl(model);
        int index = 0;
        final int maxRows = 10;
        EWBTopicModelInfoQuery query = new EWBTopicModelInfoQuery();
        query.setModelName(model);
        query.setRows(maxRows);
        while (index < count) {
            query.setStart(index);
            result.putAll(this.getTopicMetadataInternal(query).stream().map(topicMetadata -> Map.entry(topicMetadata.getId(), topicMetadata.getVocab())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            index += maxRows;
        }
        return result;
    }

    public List<EWBTopicBeta> getTopicsTopWords(String model, String topicId) {
        EWBTopicModelInfoQuery query = new EWBTopicModelInfoQuery();
        query.setModelName(model);
        return this.getTopicMetadataInternal(query).stream().filter(topicMetadata -> topicMetadata.getId().equals(topicId)).map(EWBTopicMetadata::getVocab).flatMap(Collection::stream).sorted(Comparator.comparingInt(EWBTopicBeta::getBeta).reversed()).collect(Collectors.toList());
    }

    public List<EWBSimilarityScore> getSimilarityPairs(EWBSimilarityScoreQuery query) {
        return this.ewbTMClient.get().uri("/queries/getPairsOfDocsWithHighSim/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBSimilarityScore>>() {
                }))
                .block();
    }

    private List<EWBTopicMetadata> getTopicMetadataInternal(EWBTopicModelInfoQuery query) {
        return this.ewbTMClient.get().uri("/queries/getModelInfo/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopicMetadata>>() {
                })).block();
    }

    public List<EWBTopicMetadata> getAllTopicMetadata(String model) {
        List<EWBTopicMetadata> metadataList = new ArrayList<>();
        Integer count = this.queryNrDocsColl(model);
        EWBTopicModelInfoQuery query = new EWBTopicModelInfoQuery();
        query.setModelName(model);
        query.setStart(0);
        query.setRows(count);
        return this.getTopicMetadataInternal(query);
    }

    public EWBTopicMetadata getTopicMetadata(String model, String topicId) {
        EWBTopicMetadata result = null;
        Integer count = this.queryNrDocsColl(model);
        int index = 0;
        final int maxRows = 10;
        EWBTopicModelInfoQuery query = new EWBTopicModelInfoQuery();
        query.setModelName(model);
        query.setRows(maxRows);
        while (index < count) {
            query.setStart(index);
            result = this.getTopicMetadataInternal(query).stream().filter(topicMetadata -> topicMetadata.getId().equals(topicId)).findFirst().orElse(null);
            if (result != null) {
                break;
            }
            index += maxRows;
        }
        return result;
    }

    private static final Integer MAX_TOPICS = 10;

    public List<TopicRelation> getCorrelatedTopics(EWBCorrelatedTopicsQuery query) {
        Integer numOfTopics = this.queryNrDocsColl(query.getModelCollection());
        List<TopicRelation> result = new ArrayList<>();
        List<EWBScore> tempResults;
        query.setRows(MAX_TOPICS);
        for (int i = 0; i < numOfTopics; i++) {
            int index = 0;
            List<EWBScore> totalResults = new ArrayList<>();
            query.setTopicId(i);
            do {
                query.setStart(index);
                tempResults = this.getCorrelatedTopicsInternal(query);
                totalResults.addAll(tempResults);
                index += MAX_TOPICS;
            } while (tempResults.size() == MAX_TOPICS && index < numOfTopics);
            result.add(new TopicRelation("t" + i, totalResults));
        }
        return result;
    }

    private List<EWBScore> getCorrelatedTopicsInternal(EWBCorrelatedTopicsQuery query) {
        return this.ewbTMClient.get().uri("/queries/getMostCorrelatedTopics/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBScore>>() {
                }))
                .block();
    }

    public List<PrettySemanticsModel> listSimilarDocsByText(EWBSimilarTextQuery query) {
        List<PrettySemanticsModel> result;
        List<SemanticsModel> semanticsModels = this.ewbTMClient.get().uri("/queries/getDocsSimilarToFreeText/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<SemanticsModel>>() {
                }))
                .block();
        if (semanticsModels == null)
            return null;

        CollectionQuery collectionQuery = new CollectionQuery();
        collectionQuery.setCollection(query.getCorpus());
        collectionQuery.setQ("id: (" + semanticsModels.stream().map(SemanticsModel::getId).collect(Collectors.joining(" ")) + ")");
        collectionQuery.setQop("OR");
        collectionQuery.setFl("id, title");
        collectionQuery.setRows(query.getRows());
        List<Map<String, Object>> collectedData = this.queryCollection(collectionQuery);
        result = semanticsModels.stream().sorted(Comparator.comparingDouble(SemanticsModel::getScore).reversed()).map(semanticsModel -> {
            PrettySemanticsModel prettySemanticsModel = new PrettySemanticsModel();
            Map<String, Object> doc = collectedData.stream().filter(docl -> docl.get("id").toString().equals(semanticsModel.getId())).findFirst().orElse(new HashMap<>());
            if (!doc.isEmpty()) {
                prettySemanticsModel.setId(doc.get("id").toString());
                prettySemanticsModel.setTitle(doc.get("title").toString());
                prettySemanticsModel.setScore(semanticsModel.getScore());
                return prettySemanticsModel;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return result;
    }

    public List<String> list_avail_taxonomies() {
        return this.ewbClassificationClient.get().uri("/classification/list_avail_taxonomies/")
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                }))
                .block();
    }

    @SuppressWarnings("unchecked")
    public List<ClassificationResponse> classify(ClassificationQuery query) {
        Object response = Objects.requireNonNull(this.ewbClassificationClient.get().uri("/classification/classify/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<>() {
                }))
                .block(), "Classification service response is null");
        return ClassificationModel.getResponseModel((LinkedHashMap<String, List<Object>>) response);
    }

    public boolean addTopic(RelevantTopicModel relevantTopicModel) {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = principal.getClaimAsString("sub");
        EWBRelevanceTopicModel ewbRelevanceTopicModel = new EWBRelevanceTopicModel(relevantTopicModel.getModel(), key, Integer.parseInt(relevantTopicModel.getTopicId().substring(1)));
        return Objects.requireNonNull(this.ewbTMClient.post().uri("/models/addRelevantTpcForUser", builder -> WebClientUtils.buildParameters(builder, ewbRelevanceTopicModel))
                .exchangeToMono(mono -> mono.bodyToMono(String.class)).block()).isEmpty();
    }

    public boolean removeTopic(RelevantTopicModel relevantTopicModel) {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = principal.getClaimAsString("sub");
        EWBRelevanceTopicModel ewbRelevanceTopicModel = new EWBRelevanceTopicModel(relevantTopicModel.getModel(), key, Integer.parseInt(relevantTopicModel.getTopicId().substring(1)));
        return Objects.requireNonNull(this.ewbTMClient.post().uri("/models/removeRelevantTpcForUser", builder -> WebClientUtils.buildParameters(builder, ewbRelevanceTopicModel))
                .exchangeToMono(mono -> mono.bodyToMono(String.class)).block()).isEmpty();
    }

    private List<EWBTopicMetadata> getUserTopicsInternal(RelativeTopicQuery relativeTopicQuery) {
        return this.ewbTMClient.get().uri("/queries/getUserRelevantTopics", builder -> WebClientUtils.buildParameters(builder, relativeTopicQuery))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopicMetadata>>() {
                })).block();
    }

    public List<EWBTopicMetadata> getUserTopics(String model) {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = principal.getClaimAsString("sub");
        RelativeTopicQuery relativeTopicQuery = new RelativeTopicQuery();
        relativeTopicQuery.setModel(model);
        relativeTopicQuery.setUser(key);
        return this.getUserTopicsInternal(relativeTopicQuery);
    }

    public Boolean isTopicRelevant(RelevantTopicModel relevantTopicModel) {
        Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String key = principal.getClaimAsString("sub");
        RelativeTopicQuery relativeTopicQuery = new RelativeTopicQuery();
        relativeTopicQuery.setModel(relevantTopicModel.getModel());
        relativeTopicQuery.setUser(key);
        List<EWBTopicMetadata> topics = this.getUserTopicsInternal(relativeTopicQuery);
        return topics.stream().anyMatch(s -> s.getId().equals(relevantTopicModel.getTopicId()));
    }

    public List<String> getAvailableCollections() {
        return  experts.keySet().stream().toList();
    }

    public List<ExpertModel> queryExperts(ExpertQuery query) {
        return experts.get(query.getExpertCollection()).stream()
                .filter(expertModel -> expertModel.getFirstName().contains(query.getLike()) || expertModel.getLastName().contains(query.getLike()) || expertModel.getTitle().contains(query.getLike()))
                .skip(query.getStart()).limit(query.getRows()).toList();

    }

    public Long countExperts(ExpertQuery query) {
        return experts.get(query.getExpertCollection()).stream()
                .filter(expertModel -> expertModel.getFirstName().contains(query.getLike()) || expertModel.getLastName().contains(query.getLike()) || expertModel.getTitle().contains(query.getLike()))
                .count();
    }

    public List<ExpertModel> suggestExperts(ExpertSuggestionQuery query) {
        return experts.get(query.getExpertCollection()).stream()
                .filter(expertModel -> expertModel.getAffiliation().contains(query.getText()) || expertModel.getDepartment().contains(query.getText()) || expertModel.getOrganization().contains(query.getText()) || expertModel.getScientificArea().contains(query.getText()) || expertModel.getScientificField().contains(query.getText()) || expertModel.getSubfield().contains(query.getText()) || expertModel.getKeywords().contains(query.getText()))
                .toList();
    }

    public Map<String, Object> getTopicStatistics(String corpusCollection, String model, String topicId) {

        return this.ewbTMClient.get().uri(uriBuilder -> uriBuilder.path("/queries/getTopicStatistics/")
                .queryParam("corpus_collection", corpusCollection)
                .queryParam("model_name", model)
                .queryParam("topic_id", topicId)
                .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public List<EWBTopResearcher> getTopicTopResearcher(TopicTopResearcherLookup lookup) {
        List<EWBTopResearcher> results = Objects.requireNonNull(ewbTMClient.get().uri("/queries/getTopicTopResearchers/", builder -> WebClientUtils.buildParameters(builder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopResearchersResponse>>() {
                })).block()).stream().map(researcher -> new EWBTopResearcher(researcher.getId(), researcher.getThetas() != null ? researcher.getThetas().get(0).getTheta() : 0, researcher.getWords(), researcher.getTopicRelevance(), researcher.getCounts())).collect(Collectors.toList());
        CollectionQuery collectionQuery = new CollectionQuery();
        collectionQuery.setCollection(lookup.getCorpusCollection());
        collectionQuery.setQ("id: (" + results.stream().map(EWBTopResearcher::getId).collect(Collectors.joining(" ")) + ")");
        collectionQuery.setQop("OR");
        collectionQuery.setFl("id, title");
        collectionQuery.setRows(results.size());
        List<Map<String, Object>> collectedData = this.queryCollection(collectionQuery);
        results.forEach(researcher -> researcher.setTitle(collectedData.stream().filter(cd -> cd.get("id").equals(researcher.getId())).map(cd -> cd.get("title").toString()).findFirst().orElse("N/A")));
        results.sort(Comparator.comparing((EWBTopResearcher topResearcher) -> topResearcher.getTopic() * topResearcher.getRelevance()).reversed());
        return results;
    }

    public List<EWBTopResearchGroup> getTopicTopResearchGroups(TopicTopResearchGroupLookup lookup) {
        List<EWBTopResearchGroup> results = Objects.requireNonNull(ewbTMClient.get().uri("/queries/getTopicTopRGs/", builder -> WebClientUtils.buildParameters(builder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopResearchGroupResponse>>() {
                })).block()).stream().map(rg -> new EWBTopResearchGroup(rg.getId(), rg.getThetas() != null ? rg.getThetas().get(0).getTheta() : 0, rg.getWords(), rg.getTopicRelevance(), rg.getCounts())).collect(Collectors.toList());
        CollectionQuery collectionQuery = new CollectionQuery();
        collectionQuery.setCollection(lookup.getCorpusCollection());
        collectionQuery.setQ("id: (" + results.stream().map(EWBTopResearchGroup::getId).collect(Collectors.joining(" ")) + ")");
        collectionQuery.setQop("OR");
        collectionQuery.setFl("id, title");
        collectionQuery.setRows(results.size());
        List<Map<String, Object>> collectedData = this.queryCollection(collectionQuery);
        results.forEach(rg -> rg.setTitle(collectedData.stream().filter(cd -> cd.get("id").equals(rg.getId())).map(cd -> cd.get("title").toString()).findFirst().orElse("N/A")));
        results.sort(Comparator.comparing((EWBTopResearchGroup topDoc) -> topDoc.getTopic() * topDoc.getRelevance()).reversed());
        return results;
    }

    public List<Map<String, Object>> getTopicEvolution(String corpusCollection, String model, String topicId) {
        return Objects.requireNonNull(this.ewbTMClient.get().uri(uriBuilder -> uriBuilder.path("/queries/getTopicEvolution/")
                        .queryParam("corpus_collection", corpusCollection)
                        .queryParam("model_name", model)
                        .queryParam("topic_id", topicId)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .block());
    }

    public  Map<String, Object> getMetadataAGByID(String id, String aggregatedCollectionName) {
        return this.ewbTMClient.get().uri(uriBuilder -> uriBuilder.path("/queries/getMetadataAGByID/")
                        .queryParam("id", id)
                        .queryParam("aggregated_collection_name", aggregatedCollectionName)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public List<String> getSimilarityCriteriaList() {
        return this.ewbTMClient.get().uri(uriBuilder -> uriBuilder.path("/queries/getSimiliarityCriteriaList/").build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

    public List<EWBSimilarResearcher> getResearchersSimilarToCall(ResearchSimilarToCallLookup lookup) {

        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getResearchersSimilarToCall/", builder -> WebClientUtils.buildParameters(builder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBSimilarResearcherResponse>>() {
                })).block()).stream().map(r -> new EWBSimilarResearcher(r.getId(), r.getScore(), r.getName(), r.getnPI() ,r.getnProjects(), r.getnPapers())).collect(Collectors.toList());
    }

    public List<EWBSimilarResearcher> getResearchersSimilarToText(ResearchSimilarToTextLookup lookup) {

        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getResearchersSimilarToText/", builder -> WebClientUtils.buildParameters(builder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBSimilarResearcherResponse>>() {
                })).block()).stream().map(r -> new EWBSimilarResearcher(r.getId(), r.getScore(), r.getName(), r.getnPI() ,r.getnProjects(), r.getnPapers())).collect(Collectors.toList());
    }

    public List<EWBSimilarResearchGroup> getResearchGroupsSimilarToCall(ResearchSimilarToCallLookup lookup ) {

        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getResearchGroupsSimilarToCall/", builder -> WebClientUtils.buildParameters(builder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBSimilarResearchGroupResponse>>() {
                })).block()).stream().map(rg -> new EWBSimilarResearchGroup(rg.getId(), rg.getScore(), rg.getName(), rg.getnProjects(), rg.getnPapers())).collect(Collectors.toList());
    }

    public List<EWBSimilarResearchGroup> getResearchGroupsSimilarToText(ResearchSimilarToTextLookup lookup ) {

        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getResearchGroupsSimilarToText/", builder -> WebClientUtils.buildParameters(builder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBSimilarResearchGroupResponse>>() {
                })).block()).stream().map(rg -> new EWBSimilarResearchGroup(rg.getId(), rg.getScore(), rg.getName(), rg.getnProjects(), rg.getnPapers())).collect(Collectors.toList());
    }

    public List<EWBDocAG> getAGDocsWithString(AGDocsLookup lookup) {

        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getAGDocsWithString/", builder -> WebClientUtils.buildParameters(builder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBDocAG>>() {
                })).block());
    }

    public List<EWBCallsSimilarToResearcher> getCallsSimilarToResearcher(CallsSimilarToResearcherLookup lookup) {

        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getCallsSimilarToResearcher/", builder -> WebClientUtils.buildParameters(builder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBCallsSimilarToResearcher>>() {
                })).block());
    }

    public List<EWBPrettyTheta> getThetasResearcherByID(ThetasResearcherLookup lookup) {
        String response = Objects.requireNonNull(ewbTMClient.get().uri("/queries/getThetasResearcherByID", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, lookup))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })).block()).entrySet().stream().findFirst().orElse(Map.entry("", "")).getValue();
        return this.buildThetas(response, lookup.getModelName());
    }

    private List<EWBPrettyTheta> buildThetas(String response, String modelName) {
        List<String> thetas = List.of(response.split(" "));
        List<EWBPrettyTheta> thetaList = new ArrayList<>();
        List<EWBTopicModel> topicLabels = this.getTopics(modelName);
        thetas.forEach(s -> {
            String[] values = s.split("\\|");
            if (values.length == 2) {
                if (topicLabels != null) {
                    Optional<EWBTopicModel> model = topicLabels.stream().filter(ewbTopicModel -> ewbTopicModel.getId().equals(values[0])).findFirst();
                    model.ifPresent(ewbTopicModel -> thetaList.add(new EWBPrettyTheta(values[0], ewbTopicModel.getTpcLabels(), Double.parseDouble(values[1]))));
                } else {
                    thetaList.add(new EWBPrettyTheta(values[0], values[0], Double.parseDouble(values[1])));
                }
            }
        });
        return thetaList;
    }

}
