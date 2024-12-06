import { Component, OnInit, signal } from '@angular/core';

@Component({
  selector: 'app-ewb',
  templateUrl: './ewb.component.html',
  styleUrls: ['./ewb.component.scss']
})
export class EwbComponent {
  selectedModel: string = null;
  selectedCorpus: string = null;
  viewMode: ModelViewMode = ModelViewMode.Map;
  ViewModeEnum = ModelViewMode;
  fullscreenMode = signal<boolean>(false);

  constructor() {
  }


  registerValues(ev: { corpus: string, model: string }) {
    this.selectedCorpus = ev.corpus;
    this.selectedModel = ev.model;
  }

  toggleFullscreen(){
    this.fullscreenMode.update((fullscreenMode) => !fullscreenMode)
  }

}

export enum ModelViewMode {
    'Map',
    'List'
}