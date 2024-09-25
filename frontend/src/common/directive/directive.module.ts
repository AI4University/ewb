import { NgModule } from '@angular/core';
import { MatAutocompleteOptionsScrollDirective } from './mat-auto-complete-scroll.directive';

@NgModule({
	declarations: [
	  MatAutocompleteOptionsScrollDirective
	],
	exports: [
		MatAutocompleteOptionsScrollDirective
	]
  })
  export class DirectiveModule { }
