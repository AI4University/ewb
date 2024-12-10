import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { IsActive } from '@app/core/enum/is-active.enum';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { IsActiveTypePipe } from '@app/core/formatting/pipes/is-active-type.pipe';
import { User, UserContactInfo, UserRole } from '@app/core/model/user/user.model';
import { UserService } from '@app/core/services/http/user.service';
import { AuthService } from '@app/core/services/ui/auth.service';
import { QueryParamsService } from '@app/core/services/ui/query-params.service';
import { BaseListingComponent } from '@common/base/base-listing-component';
import { PipeService } from '@common/formatting/pipe.service';
import { DataTableDateTimeFormatPipe } from '@common/formatting/pipes/date-time-format.pipe';
import { QueryResult } from '@common/model/query-result';
import { ConfirmationDialogComponent } from '@common/modules/confirmation-dialog/confirmation-dialog.component';
import { HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { ColumnDefinition, ColumnsChangedEvent, PageLoadEvent, RowActivateEvent  } from '@common/modules/listing/listing.component';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { Guid } from '@common/types/guid';
import { UserLookup } from '@idp-service/core/query/user.lookup';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { nameof } from 'ts-simple-nameof';

@Component({
	templateUrl: './user-listing.component.html',
	styleUrls: ['./user-listing.component.scss']
})
export class UserListingComponent extends BaseListingComponent<User, UserLookup> implements OnInit {
	publish = false;
	userSettingsKey = { key: 'UserListingUserSettings' };
	propertiesAvailableForOrder: ColumnDefinition[];

	@ViewChild('roleCellTemplate', { static: true }) roleCellTemplate?: TemplateRef<any>;
	// @ViewChild('nameCellTemplate', { static: true }) nameCellTemplate?: TemplateRef<any>;

	private readonly lookupFields: string[] = [
		nameof<User>(x => x.id),
		nameof<User>(x => x.firstName),
		nameof<User>(x => x.lastName),
		nameof<User>(x => x.timezone),
		nameof<User>(x => x.language),
		[nameof<User>(x => x.contacts), nameof<UserContactInfo>(x => x.id)].join('.'),
		[nameof<User>(x => x.contacts), nameof<UserContactInfo>(x => x.type)].join('.'),
		[nameof<User>(x => x.contacts), nameof<UserContactInfo>(x => x.value)].join('.'),
		[nameof<User>(x => x.globalRoles), nameof<UserRole>(x => x.id)].join('.'),
		[nameof<User>(x => x.globalRoles), nameof<UserRole>(x => x.role)].join('.'),
		nameof<User>(x => x.updatedAt),
		nameof<User>(x => x.createdAt),
		nameof<User>(x => x.hash),
		nameof<User>(x => x.isActive)
	];

	rowIdentity = x => x.id;


	constructor(
		protected router: Router,
		protected route: ActivatedRoute,
		protected uiNotificationService: UiNotificationService,
		protected httpErrorHandlingService: HttpErrorHandlingService,
		protected queryParamsService: QueryParamsService,
		private userService: UserService,
		public authService: AuthService,
		private pipeService: PipeService,
		public enumUtils: AppEnumUtils,
		private language: TranslateService,
		private dialog: MatDialog,
	) {
		super(router, route, uiNotificationService, httpErrorHandlingService, queryParamsService);
		// Lookup setup
		// Default lookup values are defined in the user settings class.
		this.lookup = this.initializeLookup();
	}

	ngOnInit() {
		super.ngOnInit();
		this.filterChanged(this.lookup);
	}

	protected initializeLookup(): UserLookup {
		const lookup = new UserLookup();
		lookup.metadata = { countAll: true };
		lookup.page = { offset: 0, size: this.ITEMS_PER_PAGE };
		lookup.isActive = [IsActive.Active];
		lookup.order = { items: [this.toDescSortField(nameof<User>(x => x.createdAt))] };
		this.updateOrderUiFields(lookup.order);

		lookup.project = {
			fields: this.lookupFields
		};

		return lookup;
	}

	protected setupColumns() {
		this.gridColumns.push(...[{
			prop: nameof<User>(x => x.firstName),
			sortable: true,
			languageName: 'USER-LISTING.FIELDS.FIRST-NAME',
		},
		{
			prop: nameof<User>(x => x.lastName),
			sortable: true,
			languageName: 'USER-LISTING.FIELDS.LAST-NAME',
		},
		{
			prop: nameof<User>(x => x.contacts),
			sortable: true,
			languageName: 'USER-LISTING.FIELDS.CONTACT-INFO',
			valueFunction: (item: User) => (item?.contacts ?? []).map(x => x.value).join(', ')
		},
		{
			prop: nameof<User>(x => x.createdAt),
			sortable: true,
			languageName: 'USER-LISTING.FIELDS.CREATED-AT',
			pipe: this.pipeService.getPipe<DataTableDateTimeFormatPipe>(DataTableDateTimeFormatPipe).withFormat('short')
		},
		{
			prop: nameof<User>(x => x.updatedAt),
			sortable: true,
			languageName: 'USER-LISTING.FIELDS.UPDATED-AT',
			pipe: this.pipeService.getPipe<DataTableDateTimeFormatPipe>(DataTableDateTimeFormatPipe).withFormat('short')
		},
		{
			prop: nameof<User>(x => x.globalRoles),
			languageName: 'USER-LISTING.FIELDS.ROLES',
			alwaysShown: true,
			maxWidth: 300,
			cellTemplate: this.roleCellTemplate
		},
		// {
		// 	prop: nameof<User>(x => x.isActive),
		// 	sortable: true,
		// 	languageName: 'USER-LISTING.FIELDS.IS-ACTIVE',
		// 	pipe: this.pipeService.getPipe<IsActiveTypePipe>(IsActiveTypePipe)
		// },
		]);
		this.propertiesAvailableForOrder = this.gridColumns.filter(x => x.sortable);
	}

	//
	// Listing Component functions
	//
	onColumnsChanged(event: ColumnsChangedEvent) {
		// super.onColumnsChanged(event);
		this.onColumnsChangedInternal(event.properties.map(x => x.toString()));
	}

	private onColumnsChangedInternal(columns: string[]) {
		// Here are defined the projection fields that always requested from the api.
		const fields = new Set(this.lookupFields);
		this.gridColumns.map(x => x.prop)
			.filter(x => !columns?.includes(x as string))
			.forEach(item => {
				fields.delete(item as string)
			});
		this.lookup.project = { fields: [...fields] };
		this.onPageLoad({ offset: 0 } as PageLoadEvent);
	}

	protected loadListing(): Observable<QueryResult<User>> {
        let lookup = this.lookup;
		return this.userService.query(lookup);
	}

	public deleteType(id: Guid) {
		if (id) {
			const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
				data: {
					isDeleteConfirmation: true,
					message: this.language.instant('GENERAL.CONFIRMATION-DIALOG.DELETE-ITEM'),
					confirmButton: this.language.instant('GENERAL.CONFIRMATION-DIALOG.ACTIONS.CONFIRM'),
					cancelButton: this.language.instant('GENERAL.CONFIRMATION-DIALOG.ACTIONS.CANCEL')
				}
			});
			dialogRef.afterClosed().pipe(takeUntil(this._destroyed)).subscribe(result => {
				if (result) {
					this.userService.delete(id).pipe(takeUntil(this._destroyed))
						.subscribe(
							complete => this.onCallbackSuccess(),
							error => this.onCallbackError(error)
						);
				}
			});
		}
	}

	onCallbackSuccess(): void {
		this.uiNotificationService.snackBarNotification(this.language.instant('GENERAL.SNACK-BAR.SUCCESSFUL-DELETE'), SnackBarNotificationLevel.Success);
		this.refresh();
	}

	onUserRowActivated(event: RowActivateEvent, baseRoute: string = null) {
		// Override default event to prevent click action
	}

	//
	// Avatar
	//
	public setDefaultAvatar(ev: Event) {
		(ev.target as HTMLImageElement).src = 'assets/images/profile-placeholder.png';
	}

}
