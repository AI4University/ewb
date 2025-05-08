import { Component, effect, input, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { TopicMetadata } from '@app/core/model/ewb/topic-metadata.model';
import { EwbService } from '@app/core/services/http/ewb.service';
import { TopicRelevanceService } from '@app/core/services/ui/topic-relevance.service';
import { BaseComponent } from '@common/base/base.component';
import { Subscription } from 'rxjs';
import { startWith, takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-topic-relevance',
  templateUrl: './topic-relevance.component.html',
  styleUrls: ['./topic-relevance.component.scss']
})
export class TopicRelevanceComponent extends BaseComponent implements OnDestroy{

	showRelevantTopics: boolean = false;
	relevantTopics: TopicMetadata[];
	allTopics: TopicMetadata[];
	model = input<string>();
    constructor(private ewbService: EwbService, private topicRelevanceService: TopicRelevanceService) {
        super();
        this.topicRelevanceService.pushTopics([]);
        effect(() => {
            const model = this.model();
            this.relevantTopics = null;
            this.allTopics = null;
            this.refreshTopics();
        },
        {
            allowSignalWrites: true,
        });
        this.topicRelevanceService.kickstartSubject.pipe(takeUntil(this._destroyed))
        .subscribe(() => {
            this.relevantTopics = null;
            this.allTopics = null;
            this.refreshTopics();
        })
    }

    refreshTopics(){
        if(this.showRelevantTopics){
            this.getRelevantTopics();
        } else {
            this.getAllTopics();
        }
    }

    getRelevantTopics(){
        this.ewbService.getAllRelativeTopics(this.model())
        .pipe(takeUntil(this._destroyed))
        .subscribe(result => {
            this.relevantTopics = result;
            this.topicRelevanceService.pushTopics(this.relevantTopics);
        });
    }

    private getAllTopics() {
        this.ewbService.getAllTopicMetadata(this.model())
        .pipe(takeUntil(this._destroyed))
        .subscribe((result: TopicMetadata[]) => {
            this.allTopics = result;
            this.topicRelevanceService.pushTopics(this.allTopics)
        });
    }
      

    removeTopic(topic: TopicMetadata, ev: any) { //(mchouliara) Shouldnt we add a confirmation dialog here?
        ev.stopPropagation();
        this.ewbService.removeRelevantTopic(this.model(), topic.id)
        .pipe(takeUntil(this._destroyed))
        .subscribe(() => {
            this.topicRelevanceService.removeTopic(topic.id);
            const idx = this.relevantTopics.findIndex((x) => x.id === topic.id);
            if(idx >= 0){ this.relevantTopics.splice(idx, 1); }
        });
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
        this.topicRelevanceService.pushTopics([]);
    }

}
