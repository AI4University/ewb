import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-corpus-model',
  templateUrl: './corpus-model.component.html',
  styleUrls: ['./corpus-model.component.scss']
})
export class CorpusModelComponent extends BaseComponent implements OnInit {

	corpusList: string[] = [];
	modelList: string[] = [];
	selectedModel: string = null;
	selectedCorpus: string = null;
	@Output() valuesSelected: EventEmitter<{corpus: string, model: string}> = new EventEmitter<{corpus: string, model: string}>();

	constructor(private ewbService: EwbService) {
	  super();
	 }

  ngOnInit(): void {
    this.ewbService.listAllCorpus()
    .pipe(takeUntil(this._destroyed))
    .subscribe((queryResult: QueryResult<string>) => {
      this.corpusList = queryResult.items;
    });
  }

  valueChanged(event: any): void {
	this.selectedModel = null;
	this.modelList = [];
    this.ewbService.listModelsByCorpus(event.value)
    .pipe(takeUntil(this._destroyed))
    .subscribe((queryResult: QueryResult<string>) => this.modelList = queryResult.items);
	this.valuesSelected.emit({corpus: this.selectedCorpus, model: this.selectedModel});
  }

  onModelSelection(event: any): void {
	this.valuesSelected.emit({
		corpus: this.selectedCorpus,
		model: this.selectedModel
	});
  }

}
