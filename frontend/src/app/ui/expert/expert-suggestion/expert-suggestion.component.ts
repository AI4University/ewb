import { Component, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ExpertSuggestionQuery } from '@app/core/query/expert-suggestion.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { ColumnDefinition, PageLoadEvent } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Component({
  selector: 'app-expert-suggestion',
  templateUrl: './expert-suggestion.component.html',
  styleUrls: ['./expert-suggestion.component.scss']
})
export class ExpertSuggestionComponent extends BaseComponent implements OnInit {
	@ViewChild('textWrapTemplate', { static: true }) textWrapTemplate: TemplateRef<any>;
	selectedDoc: any = null;
	pageOffset: number = 0;
	pageRows: number = 11;
	maxValues: number = 20;
	inputedText: string = '';
	@Input() corpus: string;
	textSimilarityQuery: ExpertSuggestionQuery = new ExpertSuggestionQuery();
	relevantDocs: any[] = [];
	selectedRelativeDoc: any = null;

	columns: ColumnDefinition[] = [];

  constructor(private ewbService: EwbService) {
	  super();
  }

  ngOnInit(): void {
	this.setupColumns();
  }

  dataFound(): boolean {
	return (this.inputedText !== undefined && this.inputedText !== null && this.inputedText.length > 0);
 }

 onTextInputed(ev: string) {
	this.inputedText = ev;
	this.pageRows = 10;
	this.pageOffset = 0;
	this.loadSimilarTextDocs();
	console.log(ev);
}

loadSimilarTextDocs() {
	this.setupTextQuery();
	this.ewbService.suggestExperts(this.textSimilarityQuery)
		.pipe(takeUntil(this._destroyed))
		.subscribe((result: QueryResult<any>) => this.registerResult(result));
}

private setupTextQuery() {
	this.textSimilarityQuery.expertCollection = this.corpus;
	this.textSimilarityQuery.text = this.inputedText;
}

  private setupColumns() {
	this.columns.push(...[
		{
			prop: nameof<any>(x => x.id),
			name: nameof<any>(x => x.id),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			canAutoResize: true,
			languageName: 'Id',
			cellTemplate: this.textWrapTemplate,
		},
		{
			prop: nameof<any>(x => x.title),
			name: nameof<any>(x => x.title),
			sortable: false,
			resizeable: false,
			alwaysShown: false,
			isTreeColumn: false,
			canAutoResize: true,
			languageName: 'Title',
			cellTemplate: this.textWrapTemplate,

		}
	]);
}

  onActiveRow(ev: any[]) {
	this.selectedRelativeDoc = ev[0];

}
private registerResult(result: QueryResult<any>) {
	this.maxValues = result.count < 10 ? result.count : 20;
	this.relevantDocs = result.items;
}

}
