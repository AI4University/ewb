import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AutoCompleteModule } from '@common/modules/auto-complete/auto-complete.module';
import { CommonFormattingModule } from '@common/formatting/common-formatting.module';
import { CommonFormsModule } from '@common/forms/common-forms.module';
import { TextFilterModule } from '@common/modules/text-filter/text-filter.module';
import { UserSettingsModule } from '@common/modules/user-settings/user-settings.module';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { UserListingFiltersComponent } from './listing/filters/user-listing-filters.component';
import { UserRoleEditorComponent } from './listing/role-editor/user-role-editor.component';
import { UserListingComponent } from './listing/user-listing.component';
import { UserRoutingModule } from './user.routing';
import { HybridListingModule } from '@common/modules/hybrid-listing/hybrid-listing.module';

@NgModule({
	declarations: [
		UserListingComponent,
		UserRoleEditorComponent,
		UserListingFiltersComponent
	],
	imports: [
		CommonModule,
		CommonUiModule,
		CommonFormsModule,
		CommonFormattingModule,
		UserRoutingModule,
		AutoCompleteModule,
		TextFilterModule,
		HybridListingModule,
		UserSettingsModule,
	],
	exports: [
		UserListingComponent,
		UserRoleEditorComponent,
		UserListingFiltersComponent
	]
})
export class UserModule { }
