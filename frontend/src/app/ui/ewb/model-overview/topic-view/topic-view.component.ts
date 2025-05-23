import { Component, Inject, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PercentValuePipe } from '@app/core/formatting/pipes/percentage.pipe';
import { TopDoc } from '@app/core/model/ewb/top-doc.model';
import { TopicBeta, TopicMetadata } from '@app/core/model/ewb/topic-metadata.model';
import { TopDocTopicQuery } from '@app/core/query/top-doc-topic.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { ColumnDefinition, ListingComponent, PageLoadEvent } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { SelectionType } from '@swimlane/ngx-datatable';
import { MetadataViewComponent } from '../../modules/metadata-view/metadata-view.component';
import { TranslateService } from '@ngx-translate/core';
import { TopicRelevanceService } from '@app/core/services/ui/topic-relevance.service';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';

@Component({
  selector: 'app-topic-view',
  templateUrl: './topic-view.component.html',
  styleUrls: ['./topic-view.component.scss']
})
export class TopicViewComponent extends BaseComponent implements OnInit {

	@ViewChild('textWrapTemplate', { static: true }) textWrapTemplate: TemplateRef<any>;
	@ViewChild('researchGroupTitleTemplate', { static: true }) researchGroupTitleTemplate: TemplateRef<any>;
	@ViewChild('researcherTitleTemplate', { static: true }) researcherTitleTemplate: TemplateRef<any>;
	@ViewChild('percentageBar', { static: true }) percentageBar: TemplateRef<any>;

	maxValue = 100;
    listingPageSize = ListingComponent.MAX_PAGE_SIZE;

	researchers: TopDoc[] = [];
	researchGroups: TopDoc[] = [];
	topResearcherColumns: ColumnDefinition[] = [];
	topicStatistics: Map<string, any> = null;
	topicName: string = '';
	words: TopicBeta[] = [];
	topWordColumns: ColumnDefinition[] = [];
	isRelevant: boolean = false;
    relevanceChanged: boolean = false;
	topResearchers: TopDoc[] = [];
	topResearchGroupColumns: ColumnDefinition[] = [];
	topResearchGroups: TopDoc[] = [];
	private sortFieldName: string = null;
	private oldSortFieldName: string = null;
	private sortResearcherOrder: string = 'desc';
	private sortResearchGroupOrder: string = 'desc';

	public selectionType = SelectionType;

	selectedWords: TopicBeta[] = [];

  constructor(
	private dialogRef: MatDialogRef<TopicViewComponent>,
	private ewbService: EwbService,
	private dialog: MatDialog,
    private language: TranslateService,
    private topicRelevanceService: TopicRelevanceService,
	public installationConfigurationService: InstallationConfigurationService,
    @Inject(MAT_DIALOG_DATA) public data: {
		corpus: string,
		model: string,
		topicId: string,
		topicName: string,
		word: string
	}) {
		super();
        this.dialogRef.beforeClosed().pipe(takeUntil(this._destroyed))
        .subscribe(() => {
            if(this.relevanceChanged){ 
                this.topicRelevanceService.kickStartRefresh()
            }
        })
	}

    private topDocTopicQuery: TopDocTopicQuery;

  ngOnInit(): void {
	this.topicName = this.data.topicName;

	this.ewbService.getTopicStatistics(this.data.corpus, this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe((result) => {
		this.topicStatistics = result;
	});

	this.topDocTopicQuery = {
		corpusCollection: this.data.corpus,
		modelName: this.data.model,
		topicId: +this.data.topicId?.split('t')[1],
		start: 0,
		rows: this.listingPageSize
	};

    this.loadResearchers();
    this.loadResearchGroups();

	this.ewbService.getTopicTopWords(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		this.words = result;
		this.maxValue = this.words.reduce((prev, curr) => (prev.beta > curr.beta)? prev : curr).beta;
		this.setupTopWordColumns();
		this.selectedWords = this.data.word !== null ? this.words.filter(topicBeta => topicBeta.id === this.data.word) : this.words;
	});

	this.ewbService.isTopicRelative(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => this.isRelevant = result);
  }

  private setupTopDocColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.topResearcherColumns.push(...[
		{
			prop: nameof<TopDoc>(x => x.name),
			name: nameof<TopDoc>(x => x.name),
			sortable: false,
			resizeable: true,
			minWidth: 100,
			alwaysShown: true,
			canAutoResize: true,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.NAME',
			cellTemplate: this.researcherTitleTemplate,
		},
		{
			prop: nameof<TopDoc>(x => x.relevance),
			name: nameof<TopDoc>(x => x.relevance),
			sortable: true,
			resizeable: true,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 250,
			minWidth: 200,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.RELEVANCE',
			cellTemplate: this.percentageBar,
			pipe: pipe
		},
		{
			prop: nameof<TopDoc>(x => x.token),
			name: nameof<TopDoc>(x => x.token),
			sortable: true,
			resizeable: true,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 100,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.WORDS',
		}
	]);
  }

  private setupTopResearchGroupColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.topResearchGroupColumns.push(...[
		{
			prop: nameof<TopDoc>(x => x.name),
			name: nameof<TopDoc>(x => x.name),
			sortable: false,
			resizeable: true,
			minWidth: 100,
			alwaysShown: true,
			canAutoResize: true,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.NAME',
			cellTemplate: this.researchGroupTitleTemplate,
		},
		{
			prop: nameof<TopDoc>(x => x.relevance),
			name: nameof<TopDoc>(x => x.relevance),
			sortable: true,
			resizeable: true,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 250,
			minWidth: 200,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.RELEVANCE',
			cellTemplate: this.percentageBar,
			pipe: pipe
		},
		{
			prop: nameof<TopDoc>(x => x.token),
			name: nameof<TopDoc>(x => x.token),
			sortable: true,
			resizeable: true,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			maxWidth: 100,
			languageName: 'APP.EWB-COMPONENT.MODEL-OVERVIEW-COMPONENT.TOPIC-VIEWER.LISTING.WORDS',
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


  showMetadata(row: any, title: string, aggregatedCollectionName: string) {
	this.dialog.open(MetadataViewComponent, {
		minWidth: 'min(600px, 90vw)',
		minHeight: '20vh',
		maxWidth: '80vw',
		maxHeight: '80vh',
		panelClass: 'topic-style',
		data: {
			selectedMetadata: row,
            title: this.language.instant(title),
			aggregatedCollectionName: aggregatedCollectionName
		}
	});
  }

  selectWord(event: any) {
  	let selectedWord: string = null;
	this.researchers.forEach(doc => doc.token = 0);
	if (event.length > 0) {

		for (let element of event) {
			selectedWord = element.id;
			this.researchers.forEach(doc => doc.token = (doc.token + (doc.counts[selectedWord] ?? 0)));
			this.oldSortFieldName = this.sortFieldName;
			this.sortFieldName = nameof<TopDoc>(x => x.token);
			this.sortResearchers();
			this.sortResearchGroup();
		}
	} else {
		this.sortFieldName = this.oldSortFieldName;
		this.sortResearchers();
		this.sortResearchGroup();
	}
  }

  sortResearcherRows(ev: any) {
	this.sortResearcherOrder = ev.newValue;
	this.sortFieldName = ev.sortDescriptors[0].property;
	this.sortResearchers();
  }

  sortResearchers() {
	this.topResearchers = [];
	this.researchers = this.researchers.sort((d0, d1) => (this.sortResearcherOrder === 'asc')? (d0[this.sortFieldName] - d1[this.sortFieldName]) : -1 * (d0[this.sortFieldName] - d1[this.sortFieldName]));
	this.topResearchers = this.researchers.slice(0, 10);
  }

  sortResearchGroupRows(ev: any) {
	this.sortResearchGroupOrder = ev.newValue;
	this.sortFieldName = ev.sortDescriptors[0].property;
	this.sortResearchGroup();
  }

  sortResearchGroup() {
	this.topResearchGroups = [];
	this.researchGroups = this.researchGroups.sort((d0, d1) => (this.sortResearchGroupOrder === 'asc')? (d0[this.sortFieldName] - d1[this.sortFieldName]) : -1 * (d0[this.sortFieldName] - d1[this.sortFieldName]));
	this.topResearchGroups = this.researchGroups.slice(0, 10);
  }
  

  addRelevant() {
	this.ewbService.addRelevantTopic(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
        this.isRelevant = true;
        this.relevanceChanged = true;
    });
  }

  removeRelevant() {
	this.ewbService.removeRelevantTopic(this.data.model, this.data.topicId)
	.pipe(takeUntil(this._destroyed))
	.subscribe(() => {
        this.isRelevant = false;
        this.relevanceChanged = true;
    });
  }

  loadResearchers(event?: PageLoadEvent){
	const start = event?.offset * event?.limit
    this.topDocTopicQuery = {
        ...this.topDocTopicQuery,
        start: start > 0 ? start: 0,
        rows: this.installationConfigurationService.getKnowledgeMapTotalResearchersDisplayed
    }
	this.ewbService.getTopResearchers(this.topDocTopicQuery)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		this.researchers = result;
		this.researchers.forEach(doc => doc.token = 0/*doc.words*/);
		this.maxValue = this.researchers.reduce((prev, curr) => (prev.topic > curr.topic)? prev : curr).relevance;
		this.topResearchers = this.researchers;
		this.setupTopDocColumns();
        // 	if (this.data.word !== null) {
        // 		this.selectWord([{id: this.data.word}]);
        // 	}
	});
  }

  loadResearchGroups(event?: PageLoadEvent){
	const start = event?.offset * event?.limit;

    this.topDocTopicQuery = {
        ...this.topDocTopicQuery,
        start: start > 0 ? start: 0,
        rows: this.installationConfigurationService.getKnowledgeMapTotalRGsDisplayed
    }
    this.ewbService.getTopResearchGroups(this.topDocTopicQuery)
	.pipe(takeUntil(this._destroyed))
	.subscribe(result => {
		this.researchGroups = result;
		this.researchGroups.forEach(doc => doc.token = 0/*doc.words*/);
		this.maxValue = this.researchGroups.reduce((prev, curr) => (prev.topic > curr.topic)? prev : curr).relevance;
		this.topResearchGroups = this.researchGroups;
		this.setupTopResearchGroupColumns();
        // 	if (this.data.word !== null) {
        // 		this.selectWord([{id: this.data.word}]);
        // 	}
	});
  }

}
