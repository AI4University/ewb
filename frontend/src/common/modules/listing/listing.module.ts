import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { DataTableHeaderFormattingService } from '@common/modules/listing/data-table-header-formatting-service';
import { ListingSettingsDialogComponent } from '@common/modules/listing/listing-settings/listing-settings-dialog.component';
import { ListingComponent } from '@common/modules/listing/listing.component';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { FilterEditorComponent } from './filter-editor/filter-editor.component';
import { MatPaginatorModule } from '@angular/material/paginator';

@NgModule({
	imports: [
		CommonUiModule,
		FormsModule,
		NgxDatatableModule,
		CommonFormsModule,
        MatPaginatorModule
	],
	declarations: [
		ListingComponent,
		ListingSettingsDialogComponent,
  		FilterEditorComponent
	],
	exports: [
		ListingComponent,
		FilterEditorComponent
	],
	providers: [
		DataTableHeaderFormattingService
	]
})
export class ListingModule {
	constructor() { }
}
