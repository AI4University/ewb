import { Injectable } from '@angular/core';
import { Classification } from '@app/core/model/ewb/classification.model';
import { HighSimDoc } from '@app/core/model/ewb/high-sim-doc.model';
import { EWBSimilarityScore } from '@app/core/model/ewb/score-similarity.model';
import { Theta } from '@app/core/model/ewb/theta.model';
import { TopDoc } from '@app/core/model/ewb/top-doc.model';
import { TopicBeta, TopicMetadata } from '@app/core/model/ewb/topic-metadata.model';
import { TopicRelation } from '@app/core/model/ewb/topic-relation.model';
import { Topic } from '@app/core/model/ewb/topic.model';
import { ClassificationQuery } from '@app/core/query/classification-query.lookup';
import { DocQuery } from '@app/core/query/docs.lookup';
import { ExpertSuggestionQuery } from '@app/core/query/expert-suggestion.lookup';
import { ExpertQuery } from '@app/core/query/experts.lookup';
import { HighSimDocLookup } from '@app/core/query/high-sim-doc.lookup';
import { EWBScoreSimilarityQuery } from '@app/core/query/score-similarity.lookup';
import { SimilatiryPairQuery } from '@app/core/query/similarities-pair-query.lookup';
import { TextSimilarityQuery } from '@app/core/query/text-similarity.lookup';
import { ThetaQuery } from '@app/core/query/theta.lookup';
import { TopDocTopicQuery } from '@app/core/query/top-doc-topic.lookup';
import { BaseHttpService } from '@common/base/base-http.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EwbService {

  private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/ewb`; }

  constructor(
    private installationConfiguration: InstallationConfigurationService,
    private http: BaseHttpService) { }

  listAllCorpus(): Observable<QueryResult<string>> {
    return this.http.get<QueryResult<string>>(`${this.apiBase}/listAllCorpus`);
  }

  listModelsByCorpus(corpus: string): Observable<QueryResult<string>> {
    return this.http.get<QueryResult<string>>(`${this.apiBase}/listModelsByCorpus`, {
      params: {
        corpus: corpus
      }
    });
  }

  listTopicsByModel(model: string): Observable<QueryResult<Topic>> {
    return this.http.get<QueryResult<Topic>>(`${this.apiBase}/topics`, {
      params: {
        model: model
      }
    });
  }

  getTopicRelations(model: string): Observable<TopicRelation[]> {
    return this.http.get<TopicRelation[]>(`${this.apiBase}/topics/relations`, {
      params: {
        model: model
      }
    });
  }

  getTopicsTemporal(corpus: string, model: string): Observable<any> {
    return this.http.get<any>(`${this.apiBase}/topics/temporal`, {
      params: {
        corpus: corpus,
        model: model
      }
    });
  }

  queryDocs(docsQuery: DocQuery): Observable<QueryResult<any>> {
    return this.http.post<QueryResult<any>>(`${this.apiBase}/queryDocuments`, docsQuery);
  }

  queryThetas(thetaQuery: ThetaQuery): Observable<QueryResult<Theta>> {
    return this.http.post<QueryResult<Theta>>(`${this.apiBase}/getThetasDoc`, thetaQuery);
  }

  getVocabularyForTopics(model: string): Observable<any> {
    return this.http.get<any>(`${this.apiBase}/topics/vocabulary`, {
      params: {
        model: model
      }
    });
  }

  getSimilarityPairs(query: SimilatiryPairQuery): Observable<any> {
    return this.http.post<any>(`${this.apiBase}/similaritiesPairs`, query);
  }

  getTopicsHierarchical(corpus: string, model: string): Observable<any> {
    return this.http.get<any>(`${this.apiBase}/topics/hierarchical`, {
      params: {
        corpus: corpus,
        model: model
      }
    });
  }

  getTopDocs(query: TopDocTopicQuery): Observable<TopDoc[]> {
    return this.http.post<TopDoc[]>(`${this.apiBase}/topics/topdocs`, query);
  }

  getTopicMetadata(model: string, topicId: string): Observable<TopicMetadata> {
    return this.http.get<TopicMetadata>(`${this.apiBase}/topics/metadata`, {
      params: {
        model: model,
        topicId: topicId
      }
    })
  }

  getAllTopicMetadata(model: string): Observable<TopicMetadata[]> {
    return this.http.get<TopicMetadata[]>(`${this.apiBase}/topics/allmetadata`, {
      params: {
        model: model
      }
    })
  }

  getTopicTopWords(model: string, topicId: string): Observable<TopicBeta[]> {
    return this.http.get<TopicBeta[]>(`${this.apiBase}/topics/topwords`, {
      params: {
        model: model,
        topicId: topicId
      }
    })
  }

  listHighSimDocs(query: HighSimDocLookup): Observable<QueryResult<HighSimDoc>> {
    return this.http.post<QueryResult<HighSimDoc>>(`${this.apiBase}/listDocsWithHighSemanticRelationship`, query);
  }

  getDocument(corpus: string, docId: string): Observable<any> {
    return this.http.get<any>(`${this.apiBase}/getDocument`, {
      params: {
        corpus_collection: corpus,
        doc_id: docId
      }
    });
  }

  listTextSimilarDocs(query: TextSimilarityQuery): Observable<QueryResult<HighSimDoc>> {
    return this.http.post<QueryResult<HighSimDoc>>(`${this.apiBase}/listDocsWithHighSimilarityToText`, query);
  }

  getPairsOfDocsWithHighSim(query: EWBScoreSimilarityQuery): Observable<QueryResult<EWBSimilarityScore>> {
    return this.http.post<QueryResult<EWBSimilarityScore>>(`${this.apiBase}/getPairsOfDocsWithHighSim`, query);
  }

  list_avail_taxonomies(): Observable<QueryResult<String>> {
    return this.http.get<QueryResult<String>>(`${this.apiBase}/list_avail_taxonomies`);
  }

  classify(query: ClassificationQuery): Observable<QueryResult<Classification>> {
    return this.http.post<QueryResult<Classification>>(`${this.apiBase}/classify`, query);
  }

  getNumOfDocs(collection: string): Observable<number> {
	return this.http.get<number>(`${this.apiBase}/getNrDocColl`, {
		params: {
			collection: collection
		}
	});
  }

  addRelevantTopic(model: string, topicId: string): Observable<TopicMetadata> {
	return this.http.post<TopicMetadata>(`${this.apiBase}/topics/addRelative`, {model: model, topicId: topicId});
  }

  removeRelevantTopic(model: string, topicId: string): Observable<void> {
	return this.http.post<void>(`${this.apiBase}/topics/removeRelative`, {model: model, topicId: topicId});
  }

  getAllRelativeTopics(model: string): Observable<TopicMetadata[]> {
	return this.http.get<TopicMetadata[]>(`${this.apiBase}/topics/relative`, {params: {
		model: model
	}});
  }

  isTopicRelative(model: string, topicId: string): Observable<boolean> {
	return this.http.post<boolean>(`${this.apiBase}/topics/isRelevant`, {model: model, topicId: topicId});
  }

  listAllExpertCollections(): Observable<QueryResult<string>> {
	return this.http.get<QueryResult<string>>(`${this.apiBase}/listAllExpertCollections`);
  }

  queryExperts(docsQuery: ExpertQuery): Observable<QueryResult<any>> {
    return this.http.post<QueryResult<any>>(`${this.apiBase}/queryExperts`, docsQuery);
  }

  suggestExperts(docsQuery: ExpertSuggestionQuery): Observable<QueryResult<any>> {
    return this.http.post<QueryResult<any>>(`${this.apiBase}/suggestExperts`, docsQuery);
  }

}
