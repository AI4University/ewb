import { CdkScrollable, ScrollDispatcher } from '@angular/cdk/scrolling';
import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { GENERAL_ANIMATIONS } from '@app/animations';
import { Theta } from '@app/core/model/ewb/theta.model';
import { Topic } from '@app/core/model/ewb/topic.model';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';

@Component({
	selector: 'app-document-search-view',
	templateUrl: './document-search-view.component.html',
	styleUrls: ['./document-search-view.component.scss'],
	animations: GENERAL_ANIMATIONS
})
export class DocumentSearchViewComponent extends BaseComponent implements OnInit, AfterViewInit {
	docs: any[] = [];
	selectedDoc: any;
	@Input() corpus: string;
	@Input() model: string;
	searchInput: string = '';
	private searchText: string = '';
	private odd: boolean = false;
	private page: number = 0;
	activeId: string | number = 0;
	maxDocs: number = 0;
	chartOptions: any = null;
	private topics: Topic[] = [];

	constructor(
		private ewbService: EwbService,
		private scrollDispatch: ScrollDispatcher,
		private sanitizer: DomSanitizer
	) {
		super();
	}
	ngAfterViewInit(): void {
		this.scrollDispatch.scrolled().subscribe((data: CdkScrollable) => {
			console.log(JSON.stringify(data));
		});
	}

	ngOnInit(): void {
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
								value: ((theta.theta / max) * 100).toFixed(2),
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

}
