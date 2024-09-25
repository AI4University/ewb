import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpertComponent } from './expert.component';
import { ExpertOverviewComponent } from './expert-overview/expert-overview.component';
import { ExpertCollectionComponent } from './expert-collection/expert-collection.component';
import { ExpertSuggestionComponent } from './expert-suggestion/expert-suggestion.component';
import { ExpertRoutingModule } from './expert-routing.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { CdkScrollableModule } from '@angular/cdk/scrolling';
import { DirectiveModule } from '@common/directive/directive.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { ExpertViewComponent } from './modules/expert-view/expert-view.component';



@NgModule({
  declarations: [
    ExpertComponent,
	ExpertOverviewComponent,
 ExpertCollectionComponent,
 ExpertSuggestionComponent,
 ExpertViewComponent
  ],
  imports: [
    CommonUiModule,
    CommonFormsModule,
	ExpertRoutingModule,
	CdkScrollableModule,
	DirectiveModule,
	ListingModule
  ]
})
export class ExpertModule { }
