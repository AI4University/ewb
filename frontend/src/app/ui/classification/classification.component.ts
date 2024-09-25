import { Component, OnInit } from '@angular/core';
import { Classification } from '@app/core/model/ewb/classification.model';
import { ClassificationQuery } from '@app/core/query/classification-query.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { ColumnDefinition } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Component({
	selector: 'app-classification',
	templateUrl: './classification.component.html',
	styleUrls: ['./classification.component.scss']
})
export class ClassificationComponent extends BaseComponent implements OnInit {

	selectedTaxonomy: string = null;
	columns: ColumnDefinition[] = [];
	text: string = null;
	data: Classification[] = [];
	dataCount: number = 0;

	runningClassification: boolean = false;

	constructor(private ewbService: EwbService) {
		super();
	}

	ngOnInit(): void {
		this.setupColumns();
	}

	onTextChange($event) {
		this.text = $event.target.value;
	}

	onClassify() {
		let query: ClassificationQuery = {
			taxonomy: this.selectedTaxonomy,
			text: this.text
		};
		this.resetData();
		this.runningClassification = true;

		this.ewbService.classify(query)
			.pipe(takeUntil(this._destroyed))
			.subscribe(response => {
				this.data = response.items;
				this.dataCount = response.count;
				this.runningClassification = false;
			});
	}

	registerValues(ev: { taxonomy: string }) {
		this.selectedTaxonomy = ev.taxonomy;
	}

	private setupColumns() {
		this.columns.push(...[
			{
				prop: nameof<Classification>(x => x.type),
				name: nameof<Classification>(x => x.type),
				sortable: false,
				resizeable: false,
				alwaysShown: true,
				canAutoResize: true,
				minWidth: 250,
				languageName: 'Code'
			},
			{
				prop: nameof<Classification>(x => x.value),
				name: nameof<Classification>(x => x.value),
				sortable: false,
				resizeable: false,
				alwaysShown: true,
				isTreeColumn: false,
				canAutoResize: true,
				minWidth: 125,
				languageName: 'Punctuation'
			}
		]);
	}

	private resetData(): void {
		this.data = [];
		this.dataCount = 0;
	}

}
