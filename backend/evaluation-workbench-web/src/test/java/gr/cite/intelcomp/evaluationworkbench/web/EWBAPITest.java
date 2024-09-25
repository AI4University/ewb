package gr.cite.intelcomp.evaluationworkbench.web;

import gr.cite.intelcomp.evaluationworkbench.model.EWBPrettyTheta;
import gr.cite.intelcomp.evaluationworkbench.model.EWBTopicModel;
import gr.cite.intelcomp.evaluationworkbench.model.PrettySemanticsModel;
import gr.cite.intelcomp.evaluationworkbench.query.*;
import gr.cite.intelcomp.evaluationworkbench.service.ewb.EWBService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
@SpringBootTest
@AutoConfigureWebFlux
@AutoConfigureMockMvc
public class EWBAPITest {

    @Autowired
    public EWBService ewbService;

    @Test
    public void testCollectionsQuery() {
        CollectionQuery query = new CollectionQuery();
        query.setCollection("cordis");
        query.setQ("*");
        query.setQop("AND");
        query.setStart(0);
        query.setRows(10);
        List<Map<String, Object>> collectionModelList = ewbService.queryCollection(query);
        Assertions.assertEquals(10, collectionModelList.size());
    }

    @Test
    public void testAllCollection() {
        List<String> collectionModelList = ewbService.getAllCollections();
        Assertions.assertEquals(3, collectionModelList.size());
    }

    @Test
    public void testAllModels() {
        List<String> collectionModelList = ewbService.getAllModels();
        Assertions.assertEquals(1, collectionModelList.size());
    }

    @Test
    public void testAllCorpus() {
        List<String> collectionModelList = ewbService.getAllCorpus();
        Assertions.assertEquals(1, collectionModelList.size());
    }

    @Test
    public void testModelForCorpora() {
        List<String> collectionModelList = ewbService.getModelsForCorpora("Cordis");
        Assertions.assertEquals(1, collectionModelList.size());
    }

    @Test
    public void testSemanticsQuery() {
        SemanticsQuery query = new SemanticsQuery();
        query.setCorpusCollection("cordis");
        query.setModelName("mallet-10");
        query.setDocId("838031");
        query.setStart(0);
        query.setRows(10);
        List<PrettySemanticsModel> collectionModelList = ewbService.querySemanticsRelationships(query);
        Assertions.assertEquals(10, collectionModelList.size());
    }

    @Test
    public void testNrDocsColl() {
        Integer result = ewbService.queryNrDocsColl("Mallet-10");
        Assertions.assertEquals(10, result.intValue());
    }

    @Test
    public void testThetas() {
        ThetasQuery query = new ThetasQuery();
        query.setCorpusCollection("Cordis");
        query.setDocId("885184");
        query.setModelName("Mallet-10");
        List<EWBPrettyTheta> result = ewbService.queryThetas(query);
        Assertions.assertEquals(2, result.size());
        //Assertions.assertEquals("t3|194 t14|7 t22|799", result);
    }

    @Test
    public void testCorpusMetadata() {
        List<String> metadata = ewbService.queryCollectionMetadata("cordis");
        Assertions.assertEquals(8, metadata.size());
    }

    @Test
    public void testDocQuery() {
        DocsQuery query = new DocsQuery();
        query.setLike("java");
        query.setCorpusCollection("Cordis");
        List<Map<String, Object>> docs = ewbService.queryDocs(query);
        Assertions.assertEquals(3, docs.size());
    }

    @Test
    public void testDocMetadata() {
        Map<String, Object> model = ewbService.getDocMetadata("Cordis", "216682");
        Assertions.assertEquals("216682", model.get("id"));
    }

    @Test
    public void testTopicQuery() {
        List<EWBTopicModel> topics = ewbService.getTopics("Mallet-10");
        Assertions.assertEquals(10, topics.size());
    }

    @Test
    public void testTopicSimilarity() {
        LargeThetasQuery largeThetasQuery = new LargeThetasQuery();
        largeThetasQuery.setCorpusCollection("cordis");
        largeThetasQuery.setModelName("mallet-10");
        largeThetasQuery.setThreshold(950);
        Map<String, Map<String, Double>> relationThetas = ewbService.getThetaRelations(largeThetasQuery);
        Assertions.assertEquals(10, relationThetas.size());
    }

    @Test
    public void testSemantics() {
        DocsQuery docsQuery = new DocsQuery();
        docsQuery.setCorpusCollection("cordis");
        docsQuery.setLike("*");
        List<String> docIds = ewbService.queryDocsInternal(docsQuery);
        SemanticsQuery query = new SemanticsQuery();
        query.setCorpusCollection("cordis");
        query.setModelName("mallet-10");
        query.setRows(10);
        List<PrettySemanticsModel> models = null;
       for (String docId: docIds) {
            query.setDocId(docId);
            models = ewbService.querySemanticsRelationships(query);
            if (models != null) {
                break;
            }
        }
       Assertions.assertNotNull(models);
    }

    @Test
    public void testTemporal() {
        Map<String, Map<Integer, Double>> temporalData = ewbService.getTemporalTopics("cordis", "mallet-10");
        Assertions.assertFalse(temporalData.isEmpty());
    }
}
