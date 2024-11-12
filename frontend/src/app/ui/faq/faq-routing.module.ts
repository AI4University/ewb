import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FaqComponent } from './faq.component';
import { AuthGuard } from '@app/core/auth-guard.service';
import { AppPermission } from '@app/core/enum/permission.enum';

const routes: Routes = [
	{
		path: '',
		component: FaqComponent,
		canActivate: [AuthGuard],
		data: {
			authContext: {
				permissions: [AppPermission.ViewFaqPage]
			}
		}
	}
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class FaqRoutingModule { }
