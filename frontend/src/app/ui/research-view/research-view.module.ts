import { NgModule } from '@angular/core';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { FormsModule } from '@angular/forms';
import { ResearchViewComponent } from './research-view.component';
import { ListingModule } from '@common/modules/listing/listing.module';


@NgModule({
  declarations: [
    ResearchViewComponent,
  ],
  imports: [
    CommonUiModule,
    FormsModule,
    ListingModule,
  ],
  exports: [
		ResearchViewComponent
	]
})
export class ResearchViewModule { }
