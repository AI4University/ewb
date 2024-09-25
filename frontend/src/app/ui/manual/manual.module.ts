import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManualComponent } from './manual.component';
import { ManualRoutingModule } from './manual-routing.module';



@NgModule({
  declarations: [
	ManualComponent
  ],
  imports: [
    CommonModule,
	ManualRoutingModule
  ]
})
export class ManualModule { }
