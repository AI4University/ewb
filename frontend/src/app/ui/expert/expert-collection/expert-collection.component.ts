import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-expert-collection',
  templateUrl: './expert-collection.component.html',
  styleUrls: ['./expert-collection.component.scss']
})
export class ExpertCollectionComponent extends BaseComponent implements OnInit {

	expertCollectionList = [];

	selectedCollection: string = null;

	@Output() valuesSelected: EventEmitter<{collection: string}> = new EventEmitter<{collection: string}>();

	constructor(private ewbService: EwbService) {
	  super();
	 }

  ngOnInit(): void {
    this.ewbService.listAllExpertCollections()
    .pipe(takeUntil(this._destroyed))
    .subscribe((queryResult: QueryResult<string>) => {
      this.expertCollectionList = queryResult.items;
    });
  }

//   valueChanged(event: any): void {
// 	//this.selectedModel = null;
// 	//this.modelList = [];
//     this.ewbService.listModelsByCorpus(event.value)
//     .pipe(takeUntil(this._destroyed))
//     .subscribe((queryResult: QueryResult<string>) => this.modelList = queryResult.items);
// 	this.valuesSelected.emit({corpus: this.selectedCorpus, model: this.selectedModel});
//   }

  onCollectionSelected(event: any): void {
	this.valuesSelected.emit({
		collection: this.selectedCollection
	});
  }

}
