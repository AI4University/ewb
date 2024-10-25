import { Component, Inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PercentValuePipe } from '@app/core/formatting/pipes/percentage.pipe';
import { TopDoc } from '@app/core/model/ewb/top-doc.model';
import { TopicBeta, TopicMetadata } from '@app/core/model/ewb/topic-metadata.model';
import { TopDocTopicQuery } from '@app/core/query/top-doc-topic.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { ColumnDefinition } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { DocumentViewComponent } from '../../modules/document-view/document-view.component';
import { SelectionType } from '@swimlane/ngx-datatable';

@Component({
  selector: 'app-topic-view',
  templateUrl: './topic-view.component.html',
  styleUrls: ['./topic-view.component.scss']
})
export class TopicViewComponent extends BaseComponent implements OnInit {

	@ViewChild('textWrapTemplate', { static: true }) textWrapTemplate: TemplateRef<any>;
	@ViewChild('percentageBar', { static: true }) percentageBar: TemplateRef<any>;

	maxValue = 100;

	documents: TopDoc[] = [];
	topDocColumns: ColumnDefinition[] = [];
	topicMetadata: TopicMetadata = null;
	topicName: string = '';
	words: TopicBeta[] = [];
	topWordColumns: ColumnDefinition[] = [];
	isRelevant: boolean = false;
	topDocuments: TopDoc[] = [];
	private sortFieldName: string = null;
	private oldSortFieldName: string = null;
	private sortOrder: string = 'desc';

	public selectionType = SelectionType;

	selectedWords: TopicBeta[] = [];

  constructor(
	private dialogRef: MatDialogRef<TopicViewComponent>,
	private ewbService: EwbService,
	private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) private data: {
		corpus: string,
		model: string,
		topicId: string,
		topicName: string,
		word: string
	}) {
		super();
	 }

  ngOnInit(): void {
	this.topicName = this.data.topicName;

	this.ewbService.getTopicMetadata(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe((result) => {
		this.topicMetadata = result;
	});

	const topDocTopicQuery: TopDocTopicQuery = {
		corpusCollection: this.data.corpus,
		modelName: this.data.model,
		topicId: +this.data.topicId.charAt(1),
		start: 0,
		rows: undefined
	};

	this.ewbService.getTopDocs(topDocTopicQuery)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		this.documents = result;
		this.documents.forEach(doc => doc.token = 0/*doc.words*/);
		this.maxValue = this.documents.reduce((prev, curr) => (prev.topic > curr.topic)? prev : curr).relevance;
		this.topDocuments = this.documents.slice(0, 10);
		this.setupTopDocColumns();
		if (this.data.word !== null) {
			this.selectWord([{id: this.data.word}]);
		}
	});

	this.ewbService.getTopicTopWords(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		this.words = result;
		this.maxValue = this.words.reduce((prev, curr) => (prev.beta > curr.beta)? prev : curr).beta;
		this.setupTopWordColumns();
		if (this.data.word !== null) {
			this.selectedWords = this.words.filter(topicBeta => topicBeta.id === this.data.word);
		}
	});

	this.ewbService.isTopicRelative(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => this.isRelevant = result);
  }

  private setupTopDocColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.topDocColumns.push(...[
		{
			prop: nameof<TopDoc>(x => x.title),
			name: nameof<TopDoc>(x => x.title),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			canAutoResize: true,
			languageName: 'Title',
			cellTemplate: this.textWrapTemplate,
			headerClass: 'pretty-header'
		},
		{
			prop: nameof<TopDoc>(x => x.relevance),
			name: nameof<TopDoc>(x => x.relevance),
			sortable: true,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 250,
			minWidth: 200,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.RELEVANCE',
			headerClass: 'pretty-header',
			cellTemplate: this.percentageBar,
			pipe: pipe
		},
		{
			prop: nameof<TopDoc>(x => x.token),
			name: nameof<TopDoc>(x => x.token),
			sortable: true,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 150,
			minWidth: 100,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.WORDS',
			headerClass: 'pretty-header'
		}
	]);
  }

  private setupTopWordColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.topWordColumns.push(...[
		{
			width: 30,
			sortable: false,
			canAutoResize: false,
			draggable: false,
			resizeable: false,
			headerCheckboxable: true,
			checkboxable: true
		},
		{
			prop: nameof<TopicBeta>(x => x.id),
			name: 'Word',
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			canAutoResize: true,
			languageName: 'Word',
			cellTemplate: this.textWrapTemplate,
			headerClass: 'pretty-header'
		},
		{
			prop: nameof<TopicBeta>(x => x.beta),
			name: nameof<TopicBeta>(x => x.beta),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			languageName: 'Weight',
			cellTemplate: this.percentageBar,
			headerClass: 'pretty-header',
			pipe: pipe
		}
	])
  }

  close() {
	this.dialogRef.close();
  }

  emptyValue(value: number): number {
	return 100 - value;
  }

  showDocument(event: any) {
	this.ewbService.getDocument(this.data.corpus, event[0].id).subscribe((doc: any) => {
		this.dialog.open(DocumentViewComponent, {
			minWidth: '80vw',
			minHeight: '80vh',
			maxWidth: '80vw',
			maxHeight: '80vh',
			panelClass: 'topic-style',
			data: {
				selectedDoc: doc
			}
		});
	});
  }

  selectWord(event: any) {
  	let selectedWord: string = null;
	this.documents.forEach(doc => doc.token = 0);
	if (event.length > 0) {

		for (let element of event) {
			selectedWord = element.id;
			this.documents.forEach(doc => doc.token = (doc.token + (doc.counts[selectedWord] !== undefined ? doc.counts[selectedWord] : 0)));
			this.oldSortFieldName = this.sortFieldName;
			this.sortFieldName = nameof<TopDoc>(x => x.token);
			this.sortDocuments();
		}
	} else {
		this.sortFieldName = this.oldSortFieldName;
		this.documents.forEach(doc => doc.token = 0/*doc.words*/);
		this.sortDocuments();
	}
  }

  sortRows(ev: any) {
	this.sortOrder = ev.newValue;
	this.sortFieldName = ev.sortDescriptors[0].property;
	this.sortDocuments();
  }

  sortDocuments() {
	this.topDocuments = [];
	this.documents = this.documents.sort((d0, d1) => (this.sortOrder === 'asc')? (d0[this.sortFieldName] - d1[this.sortFieldName]) : -1 * (d0[this.sortFieldName] - d1[this.sortFieldName]));
	this.topDocuments = this.documents.slice(0, 10);
  }

  addRelevant() {
	this.ewbService.addRelevantTopic(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => this.isRelevant = true);
  }

  removeRelevant() {
	this.ewbService.removeRelevantTopic(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(() => this.isRelevant = false);

  }

}
