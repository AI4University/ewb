import { NgModule } from '@angular/core';
import { ClassificationComponent } from './classification.component';
import { ClassificationRoutingModule } from './classification-routing.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { CorpusModelModule } from '@common/modules/corpus-model/corpus-model.module';
import { ListingModule } from '@common/modules/listing/listing.module';
import { TaxonomySelectModule } from '@common/modules/taxonomy-select/taxonomy-select.module';



@NgModule({
  declarations: [
    ClassificationComponent
  ],
  imports: [
    CommonUiModule,
    ClassificationRoutingModule,
    TaxonomySelectModule,
    ListingModule
  ]
})
export class ClassificationModule { }
