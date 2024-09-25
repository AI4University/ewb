import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-expert',
  templateUrl: './expert.component.html',
  styleUrls: ['./expert.component.scss']
})
export class ExpertComponent implements OnInit {
	selectedCollection: string = null;
	selectedCorpus: string = null;

	constructor() {
	}

	ngOnInit(): void {
	}

	registerValues(ev: { collection: string }) {
	  this.selectedCollection = ev.collection;
	}

}
