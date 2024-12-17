import { Component, EventEmitter, Inject, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PercentValuePipe } from '@app/core/formatting/pipes/percentage.pipe';
import { TopicBeta } from '@app/core/model/ewb/topic-metadata.model';
import { BaseComponent } from '@common/base/base.component';
import { ColumnDefinition, ListingComponent, PageLoadEvent } from '@common/modules/listing/listing.component';
import { nameof } from 'ts-simple-nameof';
import { SelectionType } from '@swimlane/ngx-datatable';
import { SimilarResearcher } from '@app/core/model/ewb/researcher-similar-to-call.model';
import { SimilarResearchGroup } from '@app/core/model/ewb/research-group-similar-to-call.model';
import { MetadataAgViewComponent } from '../ewb/modules/metadata-ag-view/metadata-ag-view.component';
import { TranslateService } from '@ngx-translate/core';
import { ResearchSimilarToTextPaging } from '@app/core/query/research-similar-to-text.lookup';
import { EWBCallsSimilarToResearcher } from '@app/core/model/ewb/calls-similar-to-researcher.model';

@Component({
  selector: 'app-research-view',
  templateUrl: './research-view.component.html',
  styleUrls: ['./research-view.component.scss']
})
export class ResearchViewComponent extends BaseComponent implements OnInit {

	@ViewChild('textWrapTemplate', { static: true }) textWrapTemplate: TemplateRef<any>;
	@ViewChild('percentageBar', { static: true }) percentageBar: TemplateRef<any>;
	@ViewChild('dateTemplate', { static: true }) dateTemplate: TemplateRef<any>;

	@Input() similarResearchers: SimilarResearcher[] = [];
	@Input() similarResearcherGroups: SimilarResearchGroup[] = [];
	@Input() similarToResearcher: EWBCallsSimilarToResearcher[] = [];
    @Input() pageSize: number = ListingComponent.MAX_PAGE_SIZE;

    @Output() loadResearchers = new EventEmitter<ResearchSimilarToTextPaging>();
    @Output() loadResearchGroups = new EventEmitter<ResearchSimilarToTextPaging>();
    @Output() loadResearcherTopics = new EventEmitter<ResearchSimilarToTextPaging>();

	maxValue = 100;

	similarResearchersColumns: ColumnDefinition[] = [];
	similarResearcherGroupColumns: ColumnDefinition[] = [];
    similarToResearcherColumns: ColumnDefinition[] = [];

	words: TopicBeta[] = [];
	topWordColumns: ColumnDefinition[] = [];
	private sortFieldName: string = null;
	private oldSortFieldName: string = null;
	private sortOrder: string = 'desc';

	public selectionType = SelectionType;

	selectedWords: TopicBeta[] = [];

  constructor(
	private dialog: MatDialog,
    private language: TranslateService
	) {
		super();
	 }

  ngOnInit(): void {

	this.setupSimilarResearcherColumns();
	this.setupSimilarResearchGroupColumns();
    this.setUpSimilarToResearchersColumns();
  }

  private setupSimilarResearcherColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.similarResearchersColumns.push(...[
		{
			prop: nameof<SimilarResearchGroup>(x => x.name),
			name: nameof<SimilarResearchGroup>(x => x.name),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			canAutoResize: true,
            maxWidth: 200,
			languageName: 'APP.RESEARCH-LISTING.NAME',
			cellTemplate: this.textWrapTemplate,
			headerClass: 'pretty-header'
		},
		{
			prop: nameof<SimilarResearchGroup>(x => x.score),
			name: nameof<SimilarResearchGroup>(x => x.score),
			sortable: true,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			minWidth: 150,
			languageName: 'APP.RESEARCH-LISTING.SCORE',
			headerClass: 'pretty-header',
			cellTemplate: this.percentageBar,
			pipe: pipe
		},
        {
			prop: nameof<SimilarResearcher>(x => x.nPI),
			name: nameof<SimilarResearcher>(x => x.nPI),
			sortable: true,
			resizeable: true,
			alwaysShown: false,
			canAutoResize: true,
            maxWidth: 100,
			languageName: 'APP.RESEARCH-LISTING.N-PI',
		},
        {
			prop: nameof<SimilarResearcher>(x => x.nProjects),
			name: nameof<SimilarResearcher>(x => x.nProjects),
			sortable: true,
			resizeable: true,
			alwaysShown: false,
			canAutoResize: true,
            maxWidth: 100,
			languageName: 'APP.RESEARCH-LISTING.N-PROJECTS',
		},
        {
			prop: nameof<SimilarResearcher>(x => x.nPapers),
			name: nameof<SimilarResearcher>(x => x.nPapers),
			sortable: true,
			resizeable: true,
			alwaysShown: false,
			canAutoResize: true,
            maxWidth: 100,
			languageName: 'APP.RESEARCH-LISTING.N-PAPERS',
		},
	]);
  }

  private setupSimilarResearchGroupColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.similarResearcherGroupColumns.push(...[
		{
			prop: nameof<SimilarResearchGroup>(x => x.name),
			name: nameof<SimilarResearchGroup>(x => x.name),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			canAutoResize: true,
            maxWidth: 200,
			languageName: 'APP.RESEARCH-LISTING.NAME',
			cellTemplate: this.textWrapTemplate,
			headerClass: 'pretty-header'
		},
		{
			prop: nameof<SimilarResearchGroup>(x => x.score),
			name: nameof<SimilarResearchGroup>(x => x.score),
			sortable: true,
			resizeable: false,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			minWidth: 150,
			languageName: 'APP.RESEARCH-LISTING.SCORE',
			headerClass: 'pretty-header',
			cellTemplate: this.percentageBar,
			pipe: pipe
		},
        {
			prop: nameof<SimilarResearcher>(x => x.nProjects),
			name: nameof<SimilarResearcher>(x => x.nProjects),
			sortable: true,
			resizeable: true,
			alwaysShown: false,
			canAutoResize: true,
            maxWidth: 100,
			languageName: 'APP.RESEARCH-LISTING.N-PROJECTS',
		},
        {
			prop: nameof<SimilarResearcher>(x => x.nPapers),
			name: nameof<SimilarResearcher>(x => x.nPapers),
			sortable: true,
			resizeable: true,
			alwaysShown: false,
			canAutoResize: true,
            maxWidth: 100,
			languageName: 'APP.RESEARCH-LISTING.N-PAPERS',
		},
	]);
  }

  private setUpSimilarToResearchersColumns(){
    this.similarToResearcherColumns.push(...[
        {
            prop: nameof<EWBCallsSimilarToResearcher>(x => x.name),
            name: nameof<EWBCallsSimilarToResearcher>(x => x.name),
            sortable: false,
            resizeable: true,
            alwaysShown: true,
            canAutoResize: true,
            minWidth: 200,
            languageName: 'APP.RESEARCH-LISTING.NAME',
            cellTemplate: this.textWrapTemplate,
        },
        {
            prop: nameof<EWBCallsSimilarToResearcher>(x => x.score),
            name: nameof<EWBCallsSimilarToResearcher>(x => x.score),
            sortable: true,
            resizeable: true,
            alwaysShown: true,
            isTreeColumn: false,
            canAutoResize: true,
            maxWidth: 200,
            languageName: 'APP.RESEARCH-LISTING.SCORE',
            cellTemplate: this.percentageBar,
            pipe: new PercentValuePipe()
        },
        {
            prop: nameof<EWBCallsSimilarToResearcher>(x => x.deadline),
            name: nameof<EWBCallsSimilarToResearcher>(x => x.deadline),
            sortable: true,
            resizeable: true,
            alwaysShown: true,
            isTreeColumn: false,
            canAutoResize: true,
            maxWidth: 150,
            languageName: 'APP.RESEARCH-LISTING.DEADLINE',
            cellTemplate: this.dateTemplate,
        },
    ]);
  }

  close() {
  }

  emptyValue(value: number): number {
	return 100 - value;
  }

  showMetadata(event: any, title: string) {
	this.dialog.open(MetadataAgViewComponent, {
		minWidth: '30vw',
		minHeight: '20vh',
		maxWidth: '80vw',
		maxHeight: '80vh',
		panelClass: 'topic-style',
		data: {
			selectedMetadata: event[0],
            title: this.language.instant(title)
		}
	});
  }

  sortRows(ev: any) {
	// this.sortOrder = ev.newValue;
	// this.sortFieldName = ev.sortDescriptors[0].property;
  }

  protected onResearcherLoad(event: PageLoadEvent){
    const {pageSize, limit, offset} = event;
    this.loadResearchers.emit({start: offset, rows: pageSize});
  }

  protected onResearchGroupLoad(event: PageLoadEvent){
    const {pageSize, limit, offset} = event;    
    this.loadResearchGroups.emit({start: offset, rows: pageSize});
  }

  protected onResearcherTopicsLoad(event: PageLoadEvent){
    const {pageSize, limit, offset} = event;    
    this.loadResearcherTopics.emit({start: offset, rows: pageSize});
  }

}
