import { NgModule } from '@angular/core';
import { ExpertComponent } from './expert.component';
import { RouterModule, Routes } from '@angular/router';



const routes: Routes = [
	{
		path: '',
		component: ExpertComponent
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class ExpertRoutingModule { }
