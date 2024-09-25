import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EwbComponent } from './ewb.component';
import { RouterModule, Routes } from '@angular/router';



const routes: Routes = [
	{
		path: '',
		component: EwbComponent
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class EwbRoutingModule { }
