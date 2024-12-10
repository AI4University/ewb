import { CdkScrollableModule } from '@angular/cdk/scrolling';
import { NgModule } from '@angular/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { DirectiveModule } from '@common/directive/directive.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { CorpusModelModule } from '@common/modules/corpus-model/corpus-model.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { NgxEchartsModule } from 'ngx-echarts';
import { DocumentSearchViewComponent } from './document-search-view/document-search-view.component';
import { EwbRoutingModule } from './ewb-routing.module';
import { EwbComponent } from './ewb.component';
import { ModelOverviewComponent } from './model-overview/model-overview.component';
import { TopicRelevanceComponent } from './model-overview/topic-relevance/topic-relevance.component';
import { TopicViewComponent } from './model-overview/topic-view/topic-view.component';
import { DocumentSearchComponent } from './modules/document-search/document-search.component';
import { DocumentViewComponent } from './modules/document-view/document-view.component';
import { SimilaritiesComponent } from './similarities/similarities.component';
import { TopicEvolutionChartComponent } from './model-overview/topic-view/topic-evolution-chart/topic-evolution-chart.component';
import { MetadataAgViewComponent } from './modules/metadata-ag-view/metadata-ag-view.component';
import { NgDialogAnimationService } from 'ng-dialog-animation';

@NgModule({
  declarations: [
    EwbComponent,
    ModelOverviewComponent,
    TopicViewComponent,
    TopicEvolutionChartComponent,
    DocumentSearchComponent,
    DocumentSearchViewComponent,
    SimilaritiesComponent,
    DocumentViewComponent,
    TopicRelevanceComponent,
    MetadataAgViewComponent
  ],
  imports: [
    CommonUiModule,
    CommonFormsModule,
    EwbRoutingModule,
    MatGridListModule,
    CdkScrollableModule,
    ListingModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    }),
    DirectiveModule,
    CorpusModelModule
  ],
  providers: [
    NgDialogAnimationService
  ]
})
export class EwbModule { }
