import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppPermission } from '@app/core/enum/permission.enum';

const appRoutes: Routes = [

	{ path: '', redirectTo: 'home', pathMatch: 'full' },
	{
		path: 'home',
		loadChildren: () => import('@app/ui/home/home.module').then(m => m.HomeModule),
		data: {
			authContext: {
				permissions: [AppPermission.ViewHomePage]
			}
		},
	},
	{
		path: 'ewb',
		loadChildren: () => import('@app/ui/ewb/ewb.module').then(m => m.EwbModule),
		data: {
			authContext: {
				permissions: [AppPermission.ViewEwbPage]
			}
		},
	},
	{
		path: 'recommendation-tool',
		loadChildren: () => import('@app/ui/recommendation-tool/recommendation-tool.module').then(m => m.RecommendationToolModule),
		data: {
			authContext: {
				permissions: [AppPermission.ViewRecommendationToolPage]
			}
		},
	},
//	{
//		path: 'expert',
//		canLoad: [AuthGuard],
//		loadChildren: () => import('@app/ui/expert/expert.module').then(m => m.ExpertModule)
//	},
	{
		path: 'classify',
		loadChildren: () => import('@app/ui/classification/classification.module').then(m => m.ClassificationModule),
		data: {
			authContext: {
				permissions: [AppPermission.ViewClassifyPage]
			}
		},
	},
	// {
	// 	path: 'manual',
	// 	canLoad: [AuthGuard],
	// 	loadChildren: () => import('@app/ui/manual/manual.module').then(m => m.ManualModule)
	// },
	{
		path: 'faq',
		loadChildren: () => import('@app/ui/faq/faq.module').then(m => m.FaqModule),
		data: {
			authContext: {
				permissions: [AppPermission.ViewFaqPage]
			}
		},
	},
	{
		path: 'users',
		loadChildren: () => import('./ui/user/user.module').then(m => m.UserModule),
		data: {
			authContext: {
				permissions: [AppPermission.ViewUserPage]
			}
		},
	},
	{
		path: 'login',
		loadChildren: () => import('@idp-service/ui/login/login.module').then(m => m.LoginModule)
	},
	{ path: 'logout', loadChildren: () => import('@idp-service/ui/logout/logout.module').then(m => m.LogoutModule) },
	{ path: 'unauthorized', loadChildren: () => import('@common/unauthorized/unauthorized.module').then(m => m.UnauthorizedModule) },
	{ path: '**', loadChildren: () => import('@common/page-not-found/page-not-found.module').then(m => m.PageNotFoundModule) },
];

@NgModule({
	imports: [RouterModule.forRoot(appRoutes, {})],
	exports: [RouterModule],
})
export class AppRoutingModule { }
