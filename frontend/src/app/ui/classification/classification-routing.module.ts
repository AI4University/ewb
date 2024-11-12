import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ClassificationComponent } from './classification.component';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';



const routes: Routes = [
	{
		path: '',
		component: ClassificationComponent,
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewClassifyPage]
			}
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class ClassificationRoutingModule { }
