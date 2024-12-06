import { Component, Input, OnInit } from '@angular/core';
import { TopicMetadata } from '@app/core/model/ewb/topic-metadata.model';
import { EwbService } from '@app/core/services/http/ewb.service';
import { TopicRelevanceService } from '@app/core/services/ui/topic-relevance.service';
import { BaseComponent } from '@common/base/base.component';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-topic-relevance',
  templateUrl: './topic-relevance.component.html',
  styleUrls: ['./topic-relevance.component.scss']
})
export class TopicRelevanceComponent extends BaseComponent implements OnInit {

	showRelevantTopics: boolean = false;
	topics: TopicMetadata[] = [];
	@Input() model: string;
    constructor(private ewbService: EwbService, private topicRelevanceService: TopicRelevanceService) {
        super();
    }

    ngOnInit(): void {
        this.topicRelevanceService.pushTopics({useRelevance: this.showRelevantTopics, topics: []})
        this.ewbService.getAllRelativeTopics(this.model)
        .pipe(takeUntil(this._destroyed))
        .subscribe(result => {
            this.topics = result;
            this.topicRelevanceService.pushTopics({useRelevance: this.showRelevantTopics, topics: this.topics});
        });
    }

    removeTopic(topic: TopicMetadata, ev: any) { //(mchouliara) Shouldnt we add a confirmation dialog here?
        ev.stopPropagation();
        this.ewbService.removeRelevantTopic(this.model, topic.id)
        .pipe(takeUntil(this._destroyed))
        .subscribe(() => {
            this.topics = this.topics.filter(t => t.id !== topic.id);
            this.topicRelevanceService.pushTopics({useRelevance: this.showRelevantTopics, topics: this.topics});
        });
    }

}
