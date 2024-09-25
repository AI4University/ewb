import { Component, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSelectChange } from '@angular/material/select';
import { HighSimDoc } from '@app/core/model/ewb/high-sim-doc.model';
import { HighSimDocLookup } from '@app/core/query/high-sim-doc.lookup';
import { SimilatiryPairQuery } from '@app/core/query/similarities-pair-query.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { ColumnDefinition, PageLoadEvent } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';
import { TextSimilarityQuery } from '@app/core/query/text-similarity.lookup';
import { EWBSimilarityScore } from '@app/core/model/ewb/score-similarity.model';

@Component({
	selector: 'app-similarities',
	templateUrl: './similarities.component.html',
	styleUrls: ['./similarities.component.scss']
})
export class SimilaritiesComponent extends BaseComponent implements OnInit {

	@ViewChild('textWrapTemplate', { static: true }) textWrapTemplate: TemplateRef<any>;

	@Input() model: string;
	@Input() corpus: string;
	query: SimilatiryPairQuery;
	queryForm: FormGroup;
	selectedMode: string = '-1';
	highSimQuery: HighSimDocLookup = new HighSimDocLookup();
	selectedDoc: any = null;
	pageOffset: number = 0;
	pageRows: number = 11;
	maxValues: number = 20;
	relevantDocs: HighSimDoc[] = [];
	columns: ColumnDefinition[] = [];
	selectedRelativeDoc: any = null;
	textSimilarityQuery: TextSimilarityQuery = new TextSimilarityQuery();
	inputedText: string = '';
	pairs: EWBSimilarityScore[] = [];
	pairDocs: any[] = [];


	constructor(private ewbService: EwbService, private dialog: MatDialog) {
		super();
	}

	ngOnInit(): void {
		this.query = new SimilatiryPairQuery();
		this.query.corpus = this.corpus;
		this.query.model = this.model;
		this.query.lowerLimit = 0;
		this.query.upperLimit = 100;
		this.query.records = 20;
		this.queryForm = this.query.createFormGroup();
		this.setupColumns();
	}

	onSimilarities() {
		this.pairs = [];
		this.pairDocs = [];
		this.queryForm.markAllAsTouched();
		if (!this.queryForm.valid) {
			return;
		}
		this.query = this.queryForm.value;
		this.ewbService.getSimilarityPairs(this.query)
			.pipe(takeUntil(this._destroyed))
			.subscribe(response => {
				this.maxValues = response.count < 10 ? response.count : this.query.records;
				this.pairs = response.items;
			});
	}

	getMinLimit(): number {
		this.query = this.queryForm.value;
		return this.query.lowerLimit;
	}

	sanitizeValue(ev) {
		const input: HTMLInputElement = ev.target;
		const min: number = +input.min;
		const max: number = +input.max === 0? Number.MAX_SAFE_INTEGER : +input.max;
		let value: number = +input.value;
		const formControlName: string = input.getAttribute('formControlName');
		if (value > max) {
			value = max;
		} else {
			if (formControlName === 'upperLimit') {
				if (value < this.getMinLimit()) {
					value = this.getMinLimit();
				}
			}
			if (value < min) {
				value = min;
			}
		}

		input.onchange = null;
		input.value = '' + value;
		input.onchange = (ev => this.sanitizeValue(ev));
	}


	hasPairs() {
		return this.pairs.length > 0;
	}

	onPairSelected(ev) {
		this.pairDocs = [];
		if (ev.value !== null) {
			for (let i = 0; i < 2; i++) {
				const id = i === 0 ? ev.value.id1 : ev.value.id2;
				this.ewbService.getDocument(this.corpus, id)
				.pipe(takeUntil(this._destroyed))
				.subscribe(result => {
					this.pairDocs.push(result);
				});
			}
		}
	}

	pairSelected() {
		return this.pairDocs.length > 0;
	}

	onSelectionChange(ev: MatSelectChange) {
		this.selectedMode = ev.value;
		this.selectedDoc = null;
		this.pageOffset = 0;
		this.maxValues = 20;
		this.relevantDocs = [];
		this.selectedRelativeDoc = null;
		this.inputedText = null;
		console.log(this.selectedMode);
	}

	onDocSelected(ev: any) {
		this.selectedDoc = ev;
		this.pageOffset = 0;
		this.selectedRelativeDoc = null;
		this.relevantDocs = [];
		this.loadHighSimDocs();
	}

	private registerResult(result: QueryResult<HighSimDoc>) {
		this.maxValues = result.count < 10 ? result.count : 20;
		this.relevantDocs = result.items;
	}

	private loadHighSimDocs() {
		this.setupQuery();
		this.ewbService.listHighSimDocs(this.highSimQuery)
			.pipe(takeUntil(this._destroyed))
			.subscribe((result: QueryResult<HighSimDoc>) => this.registerResult(result));

	}

	private setupQuery() {
		this.highSimQuery.corpusCollection = this.corpus;
		this.highSimQuery.modelName = this.model;
		this.highSimQuery.docId = this.selectedDoc.id;
		this.highSimQuery.start = this.pageOffset;
		this.highSimQuery.rows = this.pageRows;

	}

	private setupColumns() {
		this.columns.push(...[
			{
				prop: nameof<HighSimDoc>(x => x.id),
				name: nameof<HighSimDoc>(x => x.id),
				sortable: false,
				resizeable: false,
				alwaysShown: true,
				canAutoResize: true,
				languageName: 'Id',
				cellTemplate: this.textWrapTemplate,
			},
			{
				prop: nameof<HighSimDoc>(x => x.title),
				name: nameof<HighSimDoc>(x => x.title),
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
		this.ewbService.getDocument(this.corpus, ev[0].id)
			.pipe(takeUntil(this._destroyed))
			.subscribe(result => {
				this.selectedRelativeDoc = result;
			});
	}

	onPageLoad(ev: PageLoadEvent) {
		if (ev) {
			this.pageOffset = ev.offset * ev.pageSize;
			this.pageOffset = ev.offset === 1 ? this.pageOffset + 1 : this.pageOffset;
			this.pageRows = 10;
			switch (this.selectedMode) {
				case '1':
					this.pageRows = ev.offset === 0 ? 11 : 10;
					this.loadHighSimDocs();
					break;
				case '2':
					this.loadSimilarTextDocs();
					break;
			}
		}

	}

	dataFound(): boolean {
		return (this.selectedDoc !== undefined && this.selectedDoc !== null) || (this.inputedText !== undefined && this.inputedText !== null && this.inputedText.length > 0);
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
		this.ewbService.listTextSimilarDocs(this.textSimilarityQuery)
			.pipe(takeUntil(this._destroyed))
			.subscribe((result: QueryResult<HighSimDoc>) => this.registerResult(result));
	}

	private setupTextQuery() {
		this.textSimilarityQuery.corpus = this.corpus;
		this.textSimilarityQuery.model = this.model;
		this.textSimilarityQuery.text = this.inputedText;
		this.textSimilarityQuery.start = this.pageOffset;
		this.textSimilarityQuery.rows = this.pageRows;
	}

}
