import { FormBuilder, Validators } from "@angular/forms";

export class SimilatiryPairQuery {
	corpus: string;
    model: string;
    lowerLimit: number;
    upperLimit: number;
    year: number;
    records: number;

	private formBuilder: FormBuilder = new FormBuilder();

	public createFormGroup() {
		return this.formBuilder.group({
			corpus: [{value: this.corpus, disabled: false}, Validators.required],
			model: [{value: this.model, disabled: false}, Validators.required],
			lowerLimit: [{value: this.lowerLimit, disabled: false}, Validators.required],
			upperLimit: [{value: this.upperLimit, disabled: false}, Validators.required],
			year: [{value: this.year, disabled: false}],
			records: [{value: this.records, disabled: false}, Validators.required]
		});
	}
}
