import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { HelpService } from '@app/core/services/http/help.service';

@Component({
  selector: 'app-faq',
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.css']
})
export class FaqComponent implements OnInit {
	public data: any = '';

	constructor(private helpService: HelpService, private sanitizer: DomSanitizer) { }

	ngOnInit(): void {
	  this.helpService.getFaq().subscribe(data => {
		  const blob = new Blob([data.body], { type: 'text/html' });
		  blob.text().then(value => this.data = this.sanitizer.bypassSecurityTrustHtml(value));
	  });
	}

}
