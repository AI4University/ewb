import { Component, Input, input, OnInit } from '@angular/core';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { takeUntil } from 'rxjs/operators';

export interface EchartsOption {
	legend: Legend;
	tooltip: Tooltip;
	xAxis: Axis;
	yAxis: Axis;
	series: Series[];
}

export interface Legend {
	data: string[];
}

export interface Tooltip {
	trigger: string;
}

export interface Axis {
	type: string;
	data?: any[];
}

export interface Series {
	name: string;
	data: any[];
	type: string;
}


@Component({
	selector: 'app-topic-evolution-chart',
	templateUrl: './topic-evolution-chart.component.html',
	styleUrls: ['./topic-evolution-chart.component.scss']
})
export class TopicEvolutionChartComponent extends BaseComponent implements OnInit {

	echartsOptions: EchartsOption[] = [];
	@Input() corpus: string = null;
	@Input() model: string = null;
	@Input() topicId: string = null;

	constructor(
		private ewbService: EwbService,
	) {
		super();
	}

	ngOnInit(): void {
		this.ewbService.getTopicEvolution(this.corpus, this.model, this.topicId)
		.pipe(takeUntil(this._destroyed))
		.subscribe((results) => {
			if (results?.length > 0) {
				results.forEach(result => {
					let keys = Object.keys(result)
					if (keys?.includes('years')) {
						let series : Series[] = [];
						keys.filter(x => x != 'years')?.forEach(key => {
							series.push({
								name: key,
								data: result[key],
								type: 'line'
							})
						})
						this.echartsOptions.push({
							legend: {
								data: keys.filter(x => x != 'years')
							},
							tooltip: {
								trigger: 'axis'
							},
							xAxis: {
								type: 'category',
								data: result['years']
							},
							yAxis: {
								type: 'value'
							},
							series: series
						  });
					}
					
				})				
			}
			
		});

		
	}
}
