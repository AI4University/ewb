import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LanguageType } from '@app/core/enum/language-type.enum';
import { AppPermission } from '@app/core/enum/permission.enum';
import { ThemeType } from '@app/core/enum/theme-type.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { LanguageService } from '@app/core/services/ui/language.service';
import { ProgressIndicationService } from '@app/core/services/ui/progress-indication.service';
import { ThemingService } from '@app/core/services/ui/theming.service';
import { BaseComponent } from '@common/base/base.component';
import { Observable } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { SideNavService } from './services/side-nav.service';
import { TranslateService } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';

@Component({
	selector: 'app-navigation',
	templateUrl: './navigation.component.html',
	styleUrls: ['./navigation.component.scss'],
})
export class NavigationComponent extends BaseComponent implements OnInit {
	progressIndication = false;
	themeTypes = ThemeType;
	languageTypes = LanguageType;
	inAppNotificationCount = 0;
	appPermission = AppPermission;

	languageSelected$: Observable<string>;
	languageFlag$: Observable<string>;

	constructor(
		public authService: AuthService,
		public router: Router,
		private route: ActivatedRoute,
		public themingService: ThemingService,
		public languageService: LanguageService,
		private progressIndicationService: ProgressIndicationService,
		private enumUtils: AppEnumUtils,
        private sidenavService: SideNavService,
        private language: TranslateService,
        private dialog: MatDialog
	) {
		super();

		this.languageSelected$ = this.languageService.getLanguageChangeObservable().pipe(
			map(language => this.enumUtils.toLanguageTypeString(language)),
			takeUntil(this._destroyed)
		)

		this.languageFlag$ = this.languageService.getLanguageChangeObservable().pipe(
			map(language => this.enumUtils.toLanguageFlagPath(language)),
			takeUntil(this._destroyed)
		)

	}

	ngOnInit() {
		this.progressIndicationService.getProgressIndicationObservable().pipe(takeUntil(this._destroyed)).subscribe(x => {
			setTimeout(() => { this.progressIndication = x; });
		});
	}

    protected toggleSidebar(){
        this.sidenavService.toggle();
    }

	public logout(): void {
        const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
            data: {
                message: this.language.instant('APP.NAVIGATION.LOGOUT-DIALOG.CONFIRMATION-MESSAGE'),
                confirmButton: this.language.instant('APP.NAVIGATION.LOGOUT-DIALOG.CONFIRM-BUTTON'),
                cancelButton: this.language.instant('APP.NAVIGATION.LOGOUT-DIALOG.CANCEL')
            }
        });
        dialogRef.afterClosed().pipe(takeUntil(this._destroyed)).subscribe(result => {
            if (result) {
                this.router.navigate(['/logout']);
            }
        });
	}

	public isAuthenticated(): boolean {
		return this.authService.currentAccountIsAuthenticated();
	}

	public isTransientUser(): boolean {
		return false;
	}

	public areConsentsNeedAttention(): boolean {
		return this.authService.consentsRequireAttention();
	}

	public consentsNeedAttentionClicked() {
		this.router.navigate(['/user-profile', 'consents']);
	}

	public isAdmin(): boolean {
		return this.authService.isAdmin();
	}

	public getPrincipalName(): string {
		return this.authService.getPrincipalName() || '';
	}

	public getProfilePicture(): string {
		return 'assets/images/profile-placeholder.png';
	}

	onThemeSelected(selectedTheme: ThemeType) {
		this.themingService.themeSelected(selectedTheme);
	}

	onLanguageSelected(selectedLanguage: LanguageType) {
		this.languageService.languageSelected(selectedLanguage);
	}

	getUserProfileQueryParams(): any {
		const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || this.router.url;
		return { returnUrl: returnUrl };
	}



}
