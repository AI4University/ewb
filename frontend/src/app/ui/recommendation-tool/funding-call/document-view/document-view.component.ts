import { Component, Injector, Input, OnInit, SecurityContext } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { GENERAL_ANIMATIONS } from '@app/animations';

@Component({
  selector: 'app-document-view',
  templateUrl: './document-view.component.html',
  styleUrls: ['./document-view.component.scss'],
  animations: GENERAL_ANIMATIONS
})
export class DocumentViewComponent implements OnInit {
	@Input() selectedDoc: any = null;
	private dialogRef: MatDialogRef<DocumentViewComponent> = null;
	private dialogData: any = null;

  constructor(
	private sanitizer: DomSanitizer,
	private injector: Injector
  ) {
	this.dialogRef = this.injector.get(MatDialogRef, null);
	this.dialogData = this.injector.get(MAT_DIALOG_DATA, null);
   }

  ngOnInit(): void {
	if (this.selectedDoc === null && this.dialogData !== null) {
		this.selectedDoc = this.dialogData.selectedDoc;
	}
  }

  isArray(obj: any): boolean {
	if (typeof(obj) === 'object') {
		return obj instanceof Array;
	}
	return false;
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

}
