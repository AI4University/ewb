import { Component, OnInit } from '@angular/core';
import { EWBCallsSimilarToResearcher } from '@app/core/model/ewb/calls-similar-to-researcher.model';
import { Theta } from '@app/core/model/ewb/theta.model';
import { ResearchSimilarToTextPaging } from '@app/core/query/research-similar-to-text.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
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
	chartColors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'];

	similarToResearcher: EWBCallsSimilarToResearcher[] = [];

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
		this.chartOptions = [null, null];
		//reset criteria
		this.selectedCriteria = '';

		this.ewbService.getThetasResearcherByID({ corpusCollection: this.installationConfigurationService.getThetasResearcherByIDForChart1, id: ev.id, modelName: this.installationConfigurationService.getThetasResearcherByIDForChart1Model})
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				this.setupChartOptions(result.items, 0);
			});

		this.ewbService.getThetasResearcherByID({ corpusCollection: this.installationConfigurationService.getThetasResearcherByIDForChart2, id: ev.id, modelName: this.installationConfigurationService.getThetasResearcherByIDForChart2Model})
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				this.setupChartOptions(result.items, 1);
			});

		this.ewbService.getMetadataAGByID(ev.id, 'uc3m_researchers')
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				if (result?.length > 0) this.selectedAG = result[0];
			});
	}

	private setupChartOptions(thetas: Theta[], index: number) {
		if (thetas?.length > 0) {
			const max = thetas.map(theta => theta.theta).reduce((a, b) => a + b, 0);
			this.chartOptions[index] = {
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
						const idNumber = Number(theta?.id?.split('t')[1]) || 0;
						const data = {
							id: theta.id,
							name: `${theta.name}`,
							value: ((theta.theta / max) * 100).toFixed(2),
							itemStyle: { color: this.chartColors[idNumber % 9] ?? this.chartColors[0]},
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
			};
		}
		
	}
	typeof(obj: any): string {
		return typeof (obj);
	}

	selectedCriteriaChange(paging?: ResearchSimilarToTextPaging) {
        const query = paging ?? {rows: this.installationConfigurationService.getTotalResearchersDisplayed, start: 0}
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
