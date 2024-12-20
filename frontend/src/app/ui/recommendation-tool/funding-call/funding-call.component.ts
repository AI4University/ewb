import { CdkScrollable, ScrollDispatcher } from '@angular/cdk/scrolling';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SimilarResearchGroup } from '@app/core/model/ewb/research-group-similar-to-call.model';
import { SimilarResearcher } from '@app/core/model/ewb/researcher-similar-to-call.model';
import { Theta } from '@app/core/model/ewb/theta.model';
import { ResearchSimilarToCallLookup } from '@app/core/query/research-similar-to-call.lookup';
import { ResearchSimilarToTextPaging } from '@app/core/query/research-similar-to-text.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { QueryResult } from '@common/model/query-result';
import { ListingComponent } from '@common/modules/listing/listing.component';
import { takeUntil } from 'rxjs/operators';


@Component({
	selector: 'app-funding-call',
	templateUrl: './funding-call.component.html',
	styleUrls: ['./funding-call.component.scss']
})
export class FundingCallComponent extends BaseComponent implements OnInit {

	docs: any[] = [];
	selectedDoc: any;
	corpus: string;
	model: string;
	searchInput: string = null;

	criteriaList: string[] = [];
	selectedCriteria: string = '';

	chartOptions: any = null;

	similarResearchers: SimilarResearcher[] = [];
	similarResearcherGroups: SimilarResearchGroup[] = [];

    pageSize = ListingComponent.MAX_PAGE_SIZE;

	constructor(
		private ewbService: EwbService,
		private installationConfigurationService: InstallationConfigurationService,
	) {
		super();
		this.corpus = this.installationConfigurationService.fundingCallCorpus;
		this.model = this.installationConfigurationService.fundingCallModel;
	}
	// ngAfterViewInit(): void {
	// 	this.scrollDispatch.scrolled().subscribe((data: CdkScrollable) => {
	// 		console.log(JSON.stringify(data));
	// 	});
	// }

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
				};
			});
	}
	typeof(obj: any): string {
		return typeof (obj);
	}

	selectedCriteriaChange() {
		this.researchersChange();
        this.researchGroupsChange();
	}

    researchersChange(paging?: ResearchSimilarToTextPaging){
        if (!this.selectedDoc?.id || !this.selectedCriteria.length){ return; }
        const query: ResearchSimilarToCallLookup = {
            id: this.selectedDoc.id, 
            similarityMethod: this.selectedCriteria,
            rows: paging?.rows ?? this.pageSize,
            start: paging?.start ?? 0
        };
        this.ewbService.getResearchersSimilarToCall(query)
        .pipe(takeUntil(this._destroyed))
        .subscribe((result) => {
            this.similarResearchers = result;
            this.similarResearchers = this.similarResearchers.slice(0, 10);
            
        });
    }

    researchGroupsChange(paging?: ResearchSimilarToTextPaging){
        if (!this.selectedDoc?.id || !this.selectedCriteria.length){ return; }
        const query: ResearchSimilarToCallLookup = {
            id: this.selectedDoc.id, 
            similarityMethod: this.selectedCriteria,
            rows: paging?.rows ?? this.pageSize,
            start: paging?.start ?? 0
        };
        this.ewbService.getResearchGroupsSimilarToCall(query)
        .pipe(takeUntil(this._destroyed))
        .subscribe(result => {
            this.similarResearcherGroups = result;
            this.similarResearcherGroups = this.similarResearcherGroups.slice(0, 10);
        });
    }

}
