import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserListingComponent } from './listing/user-listing.component';
import { AppPermission } from '@app/core/enum/permission.enum';
import { AuthGuard } from '@app/core/auth-guard.service';

const routes: Routes = [
	{
		path: '',
		component: UserListingComponent,
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewUserPage]
			}
		}
	},
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
]

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule],
})
export class UserRoutingModule { }
