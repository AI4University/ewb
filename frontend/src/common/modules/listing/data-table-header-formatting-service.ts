import { DatePipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { TimezoneService } from '@app/core/services/ui/timezone.service';
import { DataTableDateFormatPipe } from '@common/formatting/pipes/date-format.pipe';
import { DataTableDateTimeFormatPipe } from '@common/formatting/pipes/date-time-format.pipe';

@Injectable()
export class DataTableHeaderFormattingService {

	constructor(private datePipe: DatePipe, private timezoneService: TimezoneService) {

	}

	getDataTableDateTimeFormatPipe(format?: string): DataTableDateTimeFormatPipe {
		return new DataTableDateTimeFormatPipe(this.datePipe, this.timezoneService).withFormat(format);
	}

	getDataTableDateFormatPipe(format?: string): DataTableDateFormatPipe {
		return new DataTableDateFormatPipe(this.datePipe).withFormat(format);
	}
}
