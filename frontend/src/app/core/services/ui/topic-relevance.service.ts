import { Injectable } from "@angular/core";
import { TopicRelevanceModel } from "@app/core/model/ewb/topic-relevance.model";
import { BehaviorSubject, Observable, Subject } from "rxjs";

@Injectable()
export class TopicRelevanceService {
	private topicRelevance: Subject<TopicRelevanceModel>  = new BehaviorSubject<TopicRelevanceModel>(new TopicRelevanceModel());

	getTopics(): Observable<TopicRelevanceModel> {
		return this.topicRelevance.asObservable();
	}

	pushTopics(topics: TopicRelevanceModel) {
		this.topicRelevance.next(topics);
	}

}
