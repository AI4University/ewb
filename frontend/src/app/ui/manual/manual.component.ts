import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { HelpService } from '@app/core/services/http/help.service';

@Component({
  selector: 'app-manual',
  templateUrl: './manual.component.html',
  styleUrls: ['./manual.component.css']
})
export class ManualComponent implements OnInit {

	public data: any = '';

  constructor(private helpService: HelpService, private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
	this.helpService.getManual().subscribe(data => {
		const blob = new Blob([data.body], { type: 'text/html' });
		blob.text().then(value => this.data = this.sanitizer.bypassSecurityTrustHtml(value));
	});
  }

}
