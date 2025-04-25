import { Component, OnInit } from '@angular/core';
import { SimilarResearchGroup } from '@app/core/model/ewb/research-group-similar-to-call.model';
import { SimilarResearcher } from '@app/core/model/ewb/researcher-similar-to-call.model';
import { ResearchSimilarToTextPaging } from '@app/core/query/research-similar-to-text.lookup';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { ListingComponent } from '@common/modules/listing/listing.component';
import * as moment from 'moment';
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
    paging: {researchers: ResearchSimilarToTextPaging, researchGroups: ResearchSimilarToTextPaging} = {
        researchers: {
            start: 0,
            rows: this.installationConfigurationService.getTotalResearchersDisplayed
        },
        researchGroups: {
            start: 0,
            rows: this.installationConfigurationService.getTotalRGsDisplayed

        }
    }

	similarResearchers: SimilarResearcher[] = [];
	similarResearcherGroups: SimilarResearchGroup[] = [];
    yearOptions: string[] = ['Any'];
    year: string = 'Any';
	
	constructor(private ewbService: EwbService, private installationConfigurationService: InstallationConfigurationService) {
		super();
        const currentYear = moment().year()
        this.yearOptions.push(...Array.apply(null, Array(50)).map((x, index) => (currentYear - index).toString()))
	}
  
	ngOnInit(): void {
		this.ewbService.getSimiliarityCriteriaList()
		  .pipe(takeUntil(this._destroyed))
		  .subscribe((result) => {
			  this.criteriaList = result;
		  });
	}

	selectedCriteriaChange() {
        this.researchersChange();
        this.researchGroupsChange();
	}


    researchersChange(paging?: ResearchSimilarToTextPaging){
        if (!this.textInput?.length || !this.selectedCriteria?.length ) { return; }
        this.paging.researchers = {
            rows: this.installationConfigurationService.getTotalResearchersDisplayed,
            start: paging?.start ?? 0
        }
        this.ewbService.getResearchersSimilarToText({
            text: this.textInput, 
            similarityMethod: this.selectedCriteria,
            year: this.year === 'Any' ? null : this.year,
            ...this.paging.researchers
        })
        .pipe(takeUntil(this._destroyed))
        .subscribe((result) => {
            this.similarResearchers = result;            
        });
    }

    researchGroupsChange(paging?: ResearchSimilarToTextPaging){
        if (!this.textInput?.length || !this.selectedCriteria?.length ) { return; }
        this.paging.researchGroups = {
            rows: this.installationConfigurationService.getTotalRGsDisplayed,
            start: paging?.start ?? 0
        }    
        this.ewbService.getResearchGroupsSimilarToText({
            text: this.textInput, 
            similarityMethod:this.selectedCriteria, 
            year: this.year === 'Any' ? null : this.year,
            ...this.paging.researchGroups
        })
        .pipe(takeUntil(this._destroyed))
        .subscribe(result => {
            this.similarResearcherGroups = result;
        });
    }
}
