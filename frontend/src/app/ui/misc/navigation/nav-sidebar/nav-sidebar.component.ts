import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AuthService } from '@app/core/services/ui/auth.service';
import { LanguageService } from '@app/core/services/ui/language.service';
import { ThemingService } from '@app/core/services/ui/theming.service';
import { BaseComponent } from '@common/base/base.component';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { takeUntil } from 'rxjs/operators';

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
		protected language: TranslateService,
        protected dialog: MatDialog
	) {
		super();
	}
    protected isAuthenticated(): boolean {
		return this.authService.currentAccountIsAuthenticated();
	}

    protected logout(): void {
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
}
