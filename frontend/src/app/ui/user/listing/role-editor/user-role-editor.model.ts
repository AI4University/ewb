import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { User, UserRolePatchPersist } from "@app/core/model/user/user.model";
import { BackendErrorValidator } from "@common/forms/validation/custom-validator";
import { ValidationErrorModel } from "@common/forms/validation/error-model/validation-error-model";
import { Validation, ValidationContext } from "@common/forms/validation/validation-context";
import { Guid } from "@common/types/guid";

export class UserRolePatchEditorModel implements UserRolePatchPersist {
	id: Guid;
	roles: String[] = [];
	hash: string;

	permissions: string[];

	public validationErrorModel: ValidationErrorModel = new ValidationErrorModel();
	protected formBuilder: UntypedFormBuilder = new UntypedFormBuilder();

	constructor() { }

	public fromModel(item: User): UserRolePatchEditorModel {
		if (item) {
			this.id = item.id;
			if (item.globalRoles != null)this.roles = item.globalRoles.map(x => x.role);
			this.hash = item.hash;
		}
		return this;
	}

	buildForm(context: ValidationContext = null, disabled: boolean = false): UntypedFormGroup {
		if (context == null) { context = this.createValidationContext(); }

		return this.formBuilder.group({
			id: [{ value: this.id, disabled: disabled }, context.getValidation('id').validators],
			roles: [{ value: this.roles, disabled: disabled }, context.getValidation('roles').validators],
			hash: [{ value: this.hash, disabled: disabled }, context.getValidation('hash').validators],
		});
	}

	createValidationContext(): ValidationContext {
		const baseContext: ValidationContext = new ValidationContext();
		const baseValidationArray: Validation[] = new Array<Validation>();
		baseValidationArray.push({ key: 'id', validators: [BackendErrorValidator(this.validationErrorModel, 'id')] });
		baseValidationArray.push({ key: 'roles', validators: [BackendErrorValidator(this.validationErrorModel, 'roles')] });
		baseValidationArray.push({ key: 'hash', validators: [] });

		baseContext.validation = baseValidationArray;
		return baseContext;
	}
}
