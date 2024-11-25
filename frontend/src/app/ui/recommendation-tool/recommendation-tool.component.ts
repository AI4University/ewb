import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '@common/base/base.component';


@Component({
	selector: 'app-recommendation-tool',
	templateUrl: './recommendation-tool.component.html',
	styleUrls: ['./recommendation-tool.component.scss']
})
export class RecommendationToolComponent extends BaseComponent implements OnInit {

	selectedTab: number = 0;

	constructor() {
		super();
	}

	ngOnInit(): void {
	}

}
