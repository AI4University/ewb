import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PopupNotification } from '@common/modules/notification/ui-notification-service';

@Component({
	selector: 'app-popup-notification-dialog',
	templateUrl: './popup-notification.component.html'
})
export class PopupNotificationDialogComponent {

	constructor(
		public dialogRef: MatDialogRef<PopupNotificationDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public notification: PopupNotification
	) {
	}
}
