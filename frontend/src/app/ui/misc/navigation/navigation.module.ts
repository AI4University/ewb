import { NgModule } from '@angular/core';
import { MatBadgeModule } from '@angular/material/badge';
import { RouterModule } from '@angular/router';
import { NavigationComponent } from '@app/ui/misc/navigation/navigation.component';
import { CommonUiModule } from '@common/ui/common-ui.module';
import { NavSidebarComponent } from './nav-sidebar/nav-sidebar.component';

@NgModule({
	imports: [
		CommonUiModule,
		RouterModule,
		MatBadgeModule,
	],
	declarations: [
		NavigationComponent,
        NavSidebarComponent
	],
	exports: [NavigationComponent, NavSidebarComponent]
})
export class NavigationModule { }
