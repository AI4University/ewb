import { computed, Injectable, signal } from "@angular/core";
import { TopicMetadata } from "@app/core/model/ewb/topic-metadata.model";
import { BehaviorSubject, Observable, Subject } from "rxjs";

@Injectable()
export class TopicRelevanceService {
    private _topics = signal<TopicMetadata[]>([]);
    public topics = computed(() => this._topics());
    private _refreshSubject = new Subject<any>();

    
	pushTopics(topics: TopicMetadata[]) {
		this._topics.set(topics);
	}

    addTopic(topic: TopicMetadata){
        this._topics.update((topics) => [...topics, topic]);
    }

    removeTopic(topicId: string){
        this._topics.update((topics) => topics.filter((x) => x.id != topicId));
    }

    get kickstartSubject(): Subject<any>{
        return this._refreshSubject
    }

    kickStartRefresh(){
        this._refreshSubject.next();
    }
}
