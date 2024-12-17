import { NgModule } from '@angular/core';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { FormsModule } from '@angular/forms';
import { ResearchViewComponent } from './research-view.component';
import { ListingModule } from '@common/modules/listing/listing.module';
import { NullifyValuePipe } from '@common/modules/hybrid-listing/pipes/nullify-value.pipe';


@NgModule({
  declarations: [
    ResearchViewComponent,
  ],
  imports: [
    CommonUiModule,
    FormsModule,
    ListingModule,
    NullifyValuePipe
  ],
  exports: [
		ResearchViewComponent
	]
})
export class ResearchViewModule { }
