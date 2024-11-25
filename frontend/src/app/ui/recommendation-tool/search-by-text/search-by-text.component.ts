import { Component, OnInit } from '@angular/core';
import { SimilarResearchGroup } from '@app/core/model/ewb/research-group-similar-to-call.model';
import { SimilarResearcher } from '@app/core/model/ewb/researcher-similar-to-call.model';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { takeUntil } from 'rxjs/operators';


@Component({
	selector: 'app-search-by-text',
	templateUrl: './search-by-text.component.html',
	styleUrls: ['./search-by-text.component.scss']
})
export class SerchByTextComponent extends BaseComponent implements OnInit {

	textInput: string = null;
	criteriaList: string[] = [];
	selectedCriteria: string = '';

	similarResearchers: SimilarResearcher[] = [];
	similarResearcherGroups: SimilarResearchGroup[] = [];
	
	constructor(private ewbService: EwbService) {
		super();
	}
  
	ngOnInit(): void {
		this.ewbService.getSimiliarityCriteriaList()
		  .pipe(takeUntil(this._destroyed))
		  .subscribe((result) => {
			  this.criteriaList = result;
		  });
	}

	selectedCriteriaChange() {
		if (this.textInput?.length > 0 && this.selectedCriteria.length > 0) {
			this.ewbService.getResearchersSimilarToText({text: this.textInput, similarityMethod: this.selectedCriteria})
			.pipe(takeUntil(this._destroyed))
			.subscribe((result) => {
				this.similarResearchers = result;
				this.similarResearchers = this.similarResearchers.slice(0, 10);
				
			});
		
			this.ewbService.getResearchGroupsSimilarToText({text: this.textInput, similarityMethod:this.selectedCriteria})
			.pipe(takeUntil(this._destroyed))
			.subscribe(result => {
				this.similarResearcherGroups = result;
				this.similarResearcherGroups = this.similarResearcherGroups.slice(0, 10);
			});
		}
	}

}
