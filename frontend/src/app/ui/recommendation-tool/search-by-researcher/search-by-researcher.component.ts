import { CdkScrollable, ScrollDispatcher } from '@angular/cdk/scrolling';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SimilarResearcher } from '@app/core/model/ewb/researcher-similar-to-call.model';
import { Theta } from '@app/core/model/ewb/theta.model';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { takeUntil } from 'rxjs/operators';


@Component({
	selector: 'app-search-by-researcher',
	templateUrl: './search-by-researcher.component.html',
	styleUrls: ['./search-by-researcher.component.scss']
})
export class SearchByResearcherComponent extends BaseComponent implements OnInit {

	agDocs: any[] = [];
	selectedAG: any;
	corpus: string = "cordis";
	model: string = "mallet-30";
	searchInput: string = null;

	criteriaList: string[] = [];
	selectedCriteria: string = '';

	chartOptions: any[] = [];

	similarResearchers: SimilarResearcher[] = [];

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

	onSelectedAGDoc(ev: any) {
		this.selectedAG = ev;
		this.chartOptions = [];

		this.ewbService.getThetasResearcherByID({ corpusCollection: 'papers', id: this.selectedAG.id})
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				this.setupChartOptions(result.items);
			});

		this.ewbService.getThetasResearcherByID({ corpusCollection: 'corpus', id: this.selectedAG.id})
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				this.setupChartOptions(result.items);
			});
	}

	private setupChartOptions(thetas: Theta[]) {
		if (thetas?.length > 0) {
			const max = thetas.map(theta => theta.theta).reduce((a, b) => a + b, 0);
			this.chartOptions.push({
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
			})
		}
		
	}
	typeof(obj: any): string {
		return typeof (obj);
	}

	selectedCriteriaChange() {
		if (this.selectedAG?.id && this.selectedCriteria.length > 0) {
			// TODO collection name parameter
			this.ewbService.getCallsSimilarToResearcher({id: this.selectedAG.id, similarityMethod: this.selectedCriteria, collectionName: "funding_calls"})
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				if (result) {
					this.similarResearchers = result.items;
					this.similarResearchers = this.similarResearchers.slice(0, 10);
				}				
			});
		}
	}

}
