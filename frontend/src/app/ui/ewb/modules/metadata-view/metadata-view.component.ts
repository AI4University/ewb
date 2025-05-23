import { Component, Injector, Input, OnInit, SecurityContext } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-metadata-view',
  templateUrl: './metadata-view.component.html',
  styleUrls: ['./metadata-view.component.scss']
})
export class MetadataViewComponent  extends BaseComponent implements OnInit {
	@Input() selectedMetadata: any = null;
	@Input() aggregatedCollectionName: any = null;
	@Input() corpusCollection: any = null;

	private dialogData: any = null;
	topicMetadata: Array<Map<string, any>>;

  constructor(
	private sanitizer: DomSanitizer,
	private injector: Injector,
	private ewbService: EwbService
  ) {
        super();
        this.dialogData = this.injector.get(MAT_DIALOG_DATA, null);
        this.selectedMetadata = this.dialogData?.selectedMetadata;
		this.aggregatedCollectionName = this.dialogData?.aggregatedCollectionName;
		this.corpusCollection = this.dialogData?.corpusCollection;
   }

  ngOnInit(): void {
	if (this.selectedMetadata != null &&  this.aggregatedCollectionName != null) {
		this.ewbService.getMetadataAGByID(this.selectedMetadata.id, this.aggregatedCollectionName)
		.pipe(takeUntil(this._destroyed))
		.subscribe((result) => {
			this.topicMetadata = result;
		});
	}
	if (this.selectedMetadata != null &&  this.corpusCollection != null) {
		this.ewbService.getDocument(this.corpusCollection, this.selectedMetadata.id)
			.pipe(takeUntil(this._destroyed))
			.subscribe(result => {
				this.topicMetadata = result;
		});
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
