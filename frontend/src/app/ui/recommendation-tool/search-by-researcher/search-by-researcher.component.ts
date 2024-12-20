import { Component, OnInit } from '@angular/core';
import { EWBCallsSimilarToResearcher } from '@app/core/model/ewb/calls-similar-to-researcher.model';
import { Theta } from '@app/core/model/ewb/theta.model';
import { ResearchSimilarToTextPaging } from '@app/core/query/research-similar-to-text.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { ListingComponent } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';


@Component({
	selector: 'app-search-by-researcher',
	templateUrl: './search-by-researcher.component.html',
	styleUrls: ['./search-by-researcher.component.scss']
})
export class SearchByResearcherComponent extends BaseComponent implements OnInit {

	agDocs: any[] = [];
	selectedAG: any;
	searchInput: string = null;

	criteriaList: string[] = [];
	selectedCriteria: string = '';

	chartOptions: any[] = [];

	similarToResearcher: EWBCallsSimilarToResearcher[] = [];

    pageSize = ListingComponent.MAX_PAGE_SIZE;

	constructor(
		private ewbService: EwbService,
		private installationConfigurationService: InstallationConfigurationService,
	) {
		super();
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
					trigger: 'item',
                    formatter: '{b}: {c}%'
				},
                legend: {
                    type: 'scroll',
                    orient: 'horizontal',
                    left: 10,
                    bottom: 10,
                    right: 10
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
							value: ((theta.theta / max) * 100).toFixed(2),
							label: {
								position: 'inner',
								formatter: `${((theta.theta / max) * 100).toFixed(2)}%`,
								overflow: 'trunacate'
							}
						}
						return data;
					}),
                    itemStyle: {
                        borderRadius: 6,
                        borderColor: '#fff',
                        borderWidth: 4
                    },
					emphasis: {
						itemStyle: {
							shadowBlur: 6,
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

	selectedCriteriaChange(paging?: ResearchSimilarToTextPaging) {
        const query = paging ?? {rows: this.pageSize, start: 0}
		if (this.selectedAG?.id && this.selectedCriteria.length > 0) {
			this.ewbService.getCallsSimilarToResearcher({
                id: this.selectedAG.id, 
                similarityMethod: this.selectedCriteria, 
                collectionName: this.installationConfigurationService.getCallsSimilarToResearcherCollectionName,
                ...query
            })
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				if (result) {
					this.similarToResearcher = result.items;
					this.similarToResearcher = this.similarToResearcher.slice(0, 10);
				}				
			});
		}
	}

}
