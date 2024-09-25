import { FilterNameDialogComponent } from '@app/ui/filter-name-dialog/filter-name-dialog.component';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { MatDialogModule } from '@angular/material/dialog';
import { NgModule } from '@angular/core';
import { CommonFormsModule } from '@common/forms/common-forms.module';



@NgModule({
	imports: [
		CommonUiModule,
		MatDialogModule,
		CommonFormsModule,
	],
	declarations: [
		FilterNameDialogComponent
	],
	exports: [
		FilterNameDialogComponent
	]
})
export class FilterNameDialogModule {
	constructor() { }
}
