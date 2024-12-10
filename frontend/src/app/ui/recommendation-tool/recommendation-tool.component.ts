import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '@common/base/base.component';


@Component({
	selector: 'app-recommendation-tool',
	templateUrl: './recommendation-tool.component.html',
	styleUrls: ['./recommendation-tool.component.scss']
})
export class RecommendationToolComponent extends BaseComponent implements OnInit {

	searchBy: SearchBy = SearchBy.FundingCall;
    SearchByEnum = SearchBy;

	constructor() {
		super();
	}

	ngOnInit(): void {
	}

}

enum SearchBy {
    FundingCall,
    Text,
    Researcher
}
