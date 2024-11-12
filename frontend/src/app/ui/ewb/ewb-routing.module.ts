import { NgModule } from '@angular/core';
import { EwbComponent } from './ewb.component';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';



const routes: Routes = [
	{
		path: '',
		component: EwbComponent,
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewEwbPage]
			}
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class EwbRoutingModule { }
