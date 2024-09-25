import { NgModule } from "@angular/core";
import { TaxonomySelectComponent } from "./taxonomy-select.component";
import { CommonUiModule } from "@common/ui/common-ui.module";

@NgModule({
  declarations: [
    TaxonomySelectComponent
  ],
  imports: [
    CommonUiModule
  ],
  exports: [
    TaxonomySelectComponent
  ]
})
export class TaxonomySelectModule { }