import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatAutocompleteActivatedEvent, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { AGDoc } from '@app/core/model/ewb/ag-doc.model';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-ag-doc-search',
  templateUrl: './ag-doc-search.component.html',
  styleUrls: ['./ag-doc-search.component.scss']
})
export class AGDocSearchComponent extends BaseComponent implements OnInit {
    @Input() label: string;
	AGdocs: AGDoc[] = [];
	@Output() selectedAGDoc: EventEmitter<AGDoc> = new EventEmitter<AGDoc>();
	searchInput: string = '';
	private searchText: string = '';
	private odd: boolean = false;
	private page: number = 0;
	activeId: string | number = 0;
	maxDocs: number = 0;

  constructor(private ewbService: EwbService) {
	  super();
  }

  ngOnInit(): void {
  }

  onChange(event: any) {
	if (event.target.value.trimEnd() !== '') {
		this.ewbService.getAGDocsWithString({aggregatedCollectionName: null, like: event.target.value.trimEnd(), rows: 10})
		.pipe(takeUntil(this._destroyed)).subscribe((queryResult: QueryResult<any>) => {
			this.searchText = event.target.value;
			this.AGdocs = queryResult.items;
			this.page = 1;
			this.activeId = 0;
			this.maxDocs = queryResult.count;
		});
	}
  }

  onSelected(event: MatAutocompleteSelectedEvent) {
	this.selectedAGDoc.emit(this.AGdocs.find(doc => doc.id === event.option.value));
	this.searchInput = event.option.getLabel();
	this.odd = !this.odd;
	this.AGdocs = [];
  }

  onActivated(event: MatAutocompleteActivatedEvent) {
	const idLimit = (this.page * 10) - 2;
	console.log(`ID Limit: ${idLimit}, Type: ${typeof(idLimit)}`);
	if (event.option !== null) {
		console.log(`ID: ${event.option.id}, Type: ${typeof(event.option.id)}`);
		if ((event.option.id as any) === idLimit) {
			console.log('I am here');
			if ((this.page * 10) < this.maxDocs) {
				this.ewbService.getAGDocsWithString({aggregatedCollectionName: null, like: this.searchText.trimEnd(), start: (this.page * 10), rows: 10})
				.pipe(takeUntil(this._destroyed)).subscribe((queryResult: QueryResult<any>) => {
					this.AGdocs = this.AGdocs.concat(queryResult.items);
					this.page++;
					this.activeId = event.option.id;
				});
			}
		}
	}
  }

  onScroll() {
	if ((this.page * 10) < this.maxDocs) {
		this.ewbService.getAGDocsWithString({aggregatedCollectionName: null, like: this.searchText.trimEnd(), start: (this.page * 10), rows: 10})
				.pipe(takeUntil(this._destroyed)).subscribe((queryResult: QueryResult<any>) => {
					this.AGdocs = this.AGdocs.concat(queryResult.items);
					this.page++;
				});
	}
  }

}
