import { Component, OnInit, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-ewb',
  templateUrl: './ewb.component.html',
  styleUrls: ['./ewb.component.scss']
})
export class EwbComponent implements OnInit {
  selectedModel: string = null;
  selectedCorpus: string = null;
  selectedTab: number = 0;

  constructor() {
  }

  ngOnInit(): void {
  }

  registerValues(ev: { corpus: string, model: string }) {
    this.selectedCorpus = ev.corpus;
    this.selectedModel = ev.model;
  }

}
