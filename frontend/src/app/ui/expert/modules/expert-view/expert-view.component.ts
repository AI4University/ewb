import { Component, Input, OnInit, SecurityContext } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-expert-view',
  templateUrl: './expert-view.component.html',
  styleUrls: ['./expert-view.component.scss']
})
export class ExpertViewComponent implements OnInit {

	@Input() selectedDoc = null;

  constructor(private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
  }

  sanitized(val: any): any {
	return this.sanitizer.sanitize(SecurityContext.HTML, val);
  }

  prettifyCamelCase(value: string): string {
	let result: string = '';
	result = result.concat(value.charAt(0).toUpperCase());
	let i: number = 1;
	while(i < value.length) {
		const character = value.charAt(i);
		if (character === character.toUpperCase()) {
			result = result.concat(' ');
		}
		result = result.concat(character);
		i++;
	}

	return result;
  }

  isArray(obj: any): boolean {
	if (typeof(obj) === 'object') {
		return obj instanceof Array;
	}
	return false;
  }

}
