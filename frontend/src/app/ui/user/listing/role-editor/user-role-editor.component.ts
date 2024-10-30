import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, UntypedFormGroup } from '@angular/forms';
import { UserService } from '@app/core/services/http/user.service';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { BaseEditor } from '@common/base/base-editor';
import { BaseComponent } from '@common/base/base.component';
import { Validation, ValidationContext } from '@common/forms/validation/validation-context';
import { TranslateService } from '@ngx-translate/core';
import { takeUntil } from 'rxjs/operators';
import { UserRolePatchEditorModel } from './user-role-editor.model';
import { AuthService } from '@app/core/services/ui/auth.service';
import { AppPermission } from '@app/core/enum/permission.enum';
import { UserRoleEditorService } from './user-role-editor.service';
import { LoggingService } from '@common/logging/logging-service';
import { FormService } from '@common/forms/form-service';
import { HttpErrorResponse } from '@angular/common/http';
import { HttpError, HttpErrorHandlingService } from '@common/modules/errors/error-handling/http-error-handling.service';
import { AppRole } from '@app/core/enum/app-role';
import { Guid } from '@common/types/guid';
import { SnackBarNotificationLevel, UiNotificationService } from '@common/modules/notification/ui-notification-service';
import { User, UserRolePatchPersist } from '@app/core/model/user/user.model';

@Component({
	selector: 'app-user-role-editor-component',
	templateUrl: './user-role-editor.component.html',
	styleUrls: ['./user-role-editor.component.scss'],
	providers: [UserRoleEditorService]
})
export class UserRoleEditorComponent extends BaseComponent implements OnInit, OnChanges {

	@Input() public item: User;

	public formGroup: UntypedFormGroup = null;
	public nowEditing = false;
	lookupParams: any;
	editorModel: UserRolePatchEditorModel;
	appRole = AppRole;
	public appRoleEnumValues = this.enumUtils.getEnumValues<AppRole>(AppRole);

	constructor(
		private language: TranslateService,
		private userService: UserService,
		private formService: FormService,
		private logger: LoggingService,
		private enumUtils: AppEnumUtils,
		private uiNotificationService: UiNotificationService,
		private authService: AuthService,
		private userRoleEditorService: UserRoleEditorService,
		private httpErrorHandlingService: HttpErrorHandlingService,
	) { super(); }

	ngOnInit() {
		this.prepareForm(this.item);
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes['item']) {
			this.prepareForm(this.item);
			this.nowEditing = false;
		}
	}

	prepareForm(data: User) {
		try {
			this.editorModel = data ? new UserRolePatchEditorModel().fromModel(data) : new UserRolePatchEditorModel();
			this.buildForm();
		} catch (error) {
			this.logger.error('Could not parse user role item: ' + data + error);
			this.uiNotificationService.snackBarNotification(this.language.instant('COMMONS.ERRORS.DEFAULT'), SnackBarNotificationLevel.Error);
		}
	}

	buildForm() {
		this.formGroup = this.editorModel.buildForm(null, this.authService.hasPermission(AppPermission.EditUser));
		this.userRoleEditorService.setValidationErrorModel(this.editorModel.validationErrorModel);
	}

	persistEntity(onSuccess?: (response) => void): void {
		const formData = this.formService.getValue(this.formGroup.value) as UserRolePatchPersist;

		this.userService.persistRoles(formData)
			.pipe(takeUntil(this._destroyed)).subscribe(
				complete => onSuccess ? onSuccess(complete) : this.onCallbackSuccess(),
				error => this.onCallbackError(error)
			);
	}

	formSubmit(): void {
		this.formService.touchAllFormFields(this.formGroup);
		if (!this.isFormValid()) {
			return;
		}

		this.persistEntity();
	}

	editItem(): void {
		this.formGroup.enable();
		this.nowEditing = true;
	}

	public isFormValid() {
		return this.formGroup.valid;
	}

	onCallbackSuccess() {
		this.nowEditing = false;
		this.formGroup.disable();
		this.uiNotificationService.snackBarNotification(this.language.instant('APP.COMMONS.SNACK-BAR.SUCCESSFUL-UPDATE'), SnackBarNotificationLevel.Success);
	}

	onCallbackError(errorResponse: HttpErrorResponse) {
		const error: HttpError = this.httpErrorHandlingService.getError(errorResponse);
		this.uiNotificationService.snackBarNotification(error.getMessagesString(), SnackBarNotificationLevel.Error);		
		if (error.statusCode === 400) {
			this.editorModel.validationErrorModel.fromJSONObject(errorResponse.error);
			this.formService.validateAllFormFields(this.formGroup);
		}
	}

	clearErrorModel() {
		this.editorModel.validationErrorModel.clear();
		this.formService.validateAllFormFields(this.formGroup);
	}
}
