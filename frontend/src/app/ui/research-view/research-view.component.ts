import { Component, Inject, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PercentValuePipe } from '@app/core/formatting/pipes/percentage.pipe';
import { TopicBeta } from '@app/core/model/ewb/topic-metadata.model';
import { BaseComponent } from '@common/base/base.component';
import { ColumnDefinition } from '@common/modules/listing/listing.component';
import { nameof } from 'ts-simple-nameof';
import { SelectionType } from '@swimlane/ngx-datatable';
import { SimilarResearcher } from '@app/core/model/ewb/researcher-similar-to-call.model';
import { SimilarResearchGroup } from '@app/core/model/ewb/research-group-similar-to-call.model';
import { MetadataAgViewComponent } from '../ewb/modules/metadata-ag-view/metadata-ag-view.component';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-research-view',
  templateUrl: './research-view.component.html',
  styleUrls: ['./research-view.component.scss']
})
export class ResearchViewComponent extends BaseComponent implements OnInit {

	@ViewChild('textWrapTemplate', { static: true }) textWrapTemplate: TemplateRef<any>;
	@ViewChild('percentageBar', { static: true }) percentageBar: TemplateRef<any>;

	maxValue = 100;

	@Input() similarResearchers: SimilarResearcher[] = [];
	@Input() similarResearcherGroups: SimilarResearchGroup[] = [];
	similarResearchersColumns: ColumnDefinition[] = [];
	similarResearcherGroupColumns: ColumnDefinition[] = [];

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

  }

  private setupSimilarResearcherColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.similarResearchersColumns.push(...[
		{
			prop: nameof<SimilarResearcher>(x => x.id),
			name: nameof<SimilarResearcher>(x => x.id),
			sortable: false,
			resizeable: true,
			alwaysShown: true,
			canAutoResize: true,
            maxWidth: 200,
			languageName: 'APP.RESEARCH-VIEW-COMPONENT.LISTING.ID',
			cellTemplate: this.textWrapTemplate,
			headerClass: 'pretty-header'
		},
		{
			prop: nameof<SimilarResearcher>(x => x.score),
			name: nameof<SimilarResearcher>(x => x.score),
			sortable: true,
			resizeable: true,
			alwaysShown: true,
			isTreeColumn: false,
			canAutoResize: true,
			minWidth: 200,
			languageName: 'APP.RESEARCH-VIEW-COMPONENT.LISTING.SCORE',
			headerClass: 'pretty-header',
			cellTemplate: this.percentageBar,
			pipe: pipe
		}
	]);
  }

  private setupSimilarResearchGroupColumns() {
	const pipe = new PercentValuePipe();
	pipe.maxValue = this.maxValue > 100 ? this.maxValue : 100;
	this.similarResearcherGroupColumns.push(...[
		{
			prop: nameof<SimilarResearchGroup>(x => x.id),
			name: nameof<SimilarResearchGroup>(x => x.id),
			sortable: false,
			resizeable: false,
			alwaysShown: true,
			canAutoResize: true,
            maxWidth: 200,
			languageName: 'APP.RESEARCH-VIEW-COMPONENT.LISTING.ID',
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
			minWidth: 200,
			languageName: 'APP.RESEARCH-VIEW-COMPONENT.LISTING.SCORE',
			headerClass: 'pretty-header',
			cellTemplate: this.percentageBar,
			pipe: pipe
		}
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
	this.sortOrder = ev.newValue;
	this.sortFieldName = ev.sortDescriptors[0].property;
  }

}
