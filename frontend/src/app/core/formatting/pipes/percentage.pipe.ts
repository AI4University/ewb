import { Pipe, PipeTransform } from '@angular/core';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';

@Pipe({ name: 'percentValue' })
export class PercentValuePipe implements PipeTransform {

	private _maxValue: number = 100;

	set maxValue(value: number) {
		this._maxValue = value;
	}

	constructor() { }

	public transform(value: number): any {
		return (value/this._maxValue) * 100;
	}
}
