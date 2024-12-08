import { CdkScrollable, ScrollDispatcher } from '@angular/cdk/scrolling';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SimilarResearchGroup } from '@app/core/model/ewb/research-group-similar-to-call.model';
import { SimilarResearcher } from '@app/core/model/ewb/researcher-similar-to-call.model';
import { Theta } from '@app/core/model/ewb/theta.model';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';


@Component({
	selector: 'app-funding-call',
	templateUrl: './funding-call.component.html',
	styleUrls: ['./funding-call.component.scss']
})
export class FundingCallComponent extends BaseComponent implements OnInit {

	docs: any[] = [];
	selectedDoc: any;
	corpus: string = "cordis";
	model: string = "mallet-30";
	searchInput: string = null;

	criteriaList: string[] = [];
	selectedCriteria: string = '';

	chartOptions: any = null;

	similarResearchers: SimilarResearcher[] = [];
	similarResearcherGroups: SimilarResearchGroup[] = [];

	constructor(
		private ewbService: EwbService,
		private scrollDispatch: ScrollDispatcher,
	) {
		super();
	}
	ngAfterViewInit(): void {
		this.scrollDispatch.scrolled().subscribe((data: CdkScrollable) => {
			console.log(JSON.stringify(data));
		});
	}

	ngOnInit(): void {
		this.ewbService.getSimiliarityCriteriaList()
		  .pipe(takeUntil(this._destroyed))
		  .subscribe((result) => {
			  this.criteriaList = result;
		  });
	}

	onSelectedDoc(ev: any) {
		this.selectedDoc = ev;
		this.setupChartOptions();
	}

	private setupChartOptions() {
		let thetas: Theta[] = [];

		this.ewbService.queryThetas({ corpusCollection: this.corpus, docId: this.selectedDoc.id, modelName: this.model })
			.pipe(takeUntil(this._destroyed))
			.subscribe((result: QueryResult<Theta>) => {
				thetas = result.items;
				const max = thetas.map(theta => theta.theta).reduce((a, b) => a + b, 0);
				this.chartOptions = {
					tooltip: {
						trigger: 'item'
					},
					legend: {
						top: '5%',
						left: 'center'
					},
					series: {
						type: 'pie',
						radius: ['40%', '70%'],
						label: {
							position: 'outside'
						},
						select: {
							disabled: true
						},
						data: thetas.map((theta: Theta) => {
							const data = {
								id: theta.id,
								name: `${theta.name}`,
								value: (theta.theta / max) * 100,
								label: {
									position: 'inner',
									formatter: `${((theta.theta / max) * 100).toFixed(2)}%`,
									overflow: 'trunacate'
								}
							}
							return data;
						}),
						emphasis: {
							itemStyle: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: 'rgba(0, 0, 0, 0.5)'
							}
						}
					}
				};
			});
	}
	typeof(obj: any): string {
		return typeof (obj);
	}

	selectedCriteriaChange() {
		if (this.selectedDoc?.id && this.selectedCriteria.length > 0) {
			this.ewbService.getResearchersSimilarToCall({id: this.selectedDoc.id, similarityMethod: this.selectedCriteria})
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				this.similarResearchers = result;
				this.similarResearchers = this.similarResearchers.slice(0, 10);
				
			});
		
			this.ewbService.getResearchGroupsSimilarToCall({id: this.selectedDoc.id, similarityMethod: this.selectedCriteria})
			.pipe(takeUntil(this._destroyed))
			.subscribe(result => {
				this.similarResearcherGroups = result;
				this.similarResearcherGroups = this.similarResearcherGroups.slice(0, 10);
			});
		}
	}

}
