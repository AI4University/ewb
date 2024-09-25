import { Component, EventEmitter, Input, OnInit, Output, SecurityContext } from '@angular/core';
import { MatAutocompleteActivatedEvent, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { DomSanitizer } from '@angular/platform-browser';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-expert-overview',
  templateUrl: './expert-overview.component.html',
  styleUrls: ['./expert-overview.component.scss']
})
export class ExpertOverviewComponent extends BaseComponent implements OnInit {
	docs: any[] = [];
	selectedDoc = null;
	@Input() corpus: string;
	searchInput: string = '';
	private searchText: string = '';
	private odd: boolean = false;
	private page: number = 0;
	activeId: string | number = 0;
	maxDocs: number = 0;

  constructor(
	private ewbService: EwbService,
	private sanitizer: DomSanitizer) {
	  super();
  }

  ngOnInit(): void {
  }

  onChange(event: any) {
	if (event.target.value.trimEnd() !== '') {
		this.ewbService.queryExperts({expertCollection: this.corpus, like: event.target.value.trimEnd(), rows: 10})
		.pipe(takeUntil(this._destroyed)).subscribe((queryResult: QueryResult<any>) => {
			this.searchText = event.target.value;
			this.docs = queryResult.items;
			this.page = 1;
			this.activeId = 0;
			this.maxDocs = queryResult.count;
		});
	}
  }

  onSelected(event: MatAutocompleteSelectedEvent) {
	this.selectedDoc = this.docs.find(doc => doc.id === event.option.value);
	this.searchInput = event.option.getLabel();
	//this.setupChartOptions();
	// if (!this.odd) {
	// 	this.searchInput = this.searchText + ' ';
	// } else {
	// 	this.searchInput = this.searchText.trimEnd();
	// }
	this.odd = !this.odd;
	this.docs = [];
  }

  onActivated(event: MatAutocompleteActivatedEvent) {
	const idLimit = (this.page * 10) - 2;
	console.log(`ID Limit: ${idLimit}, Type: ${typeof(idLimit)}`);
	if (event.option !== null) {
		console.log(`ID: ${event.option.id}, Type: ${typeof(event.option.id)}`);
		if ((event.option.id as any) === idLimit) {
			console.log('I am here');
			if ((this.page * 10) < this.maxDocs) {
				this.ewbService.queryExperts({expertCollection: this.corpus, like: this.searchText.trimEnd(), start: (this.page * 10), rows: 10})
				.pipe(takeUntil(this._destroyed)).subscribe((queryResult: QueryResult<any>) => {
					this.docs = this.docs.concat(queryResult.items);
					this.page++;
					this.activeId = event.option.id;
				});
			}
		}
	}
  }

  onScroll() {
	if ((this.page * 10) < this.maxDocs) {
		this.ewbService.queryExperts({expertCollection: this.corpus, like: this.searchText.trimEnd(), start: (this.page * 10), rows: 10})
				.pipe(takeUntil(this._destroyed)).subscribe((queryResult: QueryResult<any>) => {
					this.docs = this.docs.concat(queryResult.items);
					this.page++;
				});
	}
  }




}
