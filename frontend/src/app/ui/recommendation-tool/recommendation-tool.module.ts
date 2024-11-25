import { NgModule } from '@angular/core';
import { RecommendationToolComponent } from './recommendation-tool.component';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { RecommendationToolRoutingModule } from './recommendation-tool-routing.module';
import { FundingCallComponent } from './funding-call/funding-call.component';
import { DocumentSearchComponent } from './funding-call/document-search/document-search.component';
import { DocumentViewComponent } from './funding-call/document-view/document-view.component';
import { NgxEchartsModule } from 'ngx-echarts';
import { FormsModule } from '@angular/forms';
import { ResearchViewModule } from '../research-view/research-view.module';
import { SerchByTextComponent } from './search-by-text/search-by-text.component';
import { AGDocSearchComponent } from './search-by-researcher/ag-doc-search/ag-doc-search.component';
import { SearchByResearcherComponent } from './search-by-researcher/search-by-researcher.component';



@NgModule({
  declarations: [
    RecommendationToolComponent,
    FundingCallComponent,
    SerchByTextComponent,
    DocumentSearchComponent,
    DocumentViewComponent,
    SearchByResearcherComponent,
    AGDocSearchComponent
  ],
  imports: [
    CommonUiModule,
    RecommendationToolRoutingModule,
    ListingModule,
    FormsModule,
    ResearchViewModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    }),
  ]
})
export class RecommendationToolModule { }
