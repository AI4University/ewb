import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AuthService } from '@app/core/services/ui/auth.service';
import { LanguageService } from '@app/core/services/ui/language.service';
import { ThemingService } from '@app/core/services/ui/theming.service';
import { BaseComponent } from '@common/base/base.component';

@Component({
  selector: 'app-nav-sidebar',
  templateUrl: './nav-sidebar.component.html',
  styleUrl: './nav-sidebar.component.scss'
})
export class NavSidebarComponent extends BaseComponent{
	progressIndication = false;
	appPermission = AppPermission;

	constructor(
		protected authService: AuthService,
		protected router: Router,
		protected themingService: ThemingService,
		protected languageService: LanguageService,
	) {
		super();
	}
    protected isAuthenticated(): boolean {
		return this.authService.currentAccountIsAuthenticated();
	}
}
