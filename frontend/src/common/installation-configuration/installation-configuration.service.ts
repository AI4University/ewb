
import { Injectable } from '@angular/core';
import { LanguageType } from '@app/core/enum/language-type.enum';
import { ThemeType } from '@app/core/enum/theme-type.enum';
import { BaseHttpService } from '@common/base/base-http.service';
import { BaseComponent } from '@common/base/base.component';
import { BaseHttpParams } from '@common/http/base-http-params';
import { InterceptorType } from '@common/http/interceptors/interceptor-type';
import { LogLevel } from '@common/logging/logging-service';
import { KeycloakFlow } from 'keycloak-js';
import { Observable, throwError } from 'rxjs';
import { catchError, takeUntil } from 'rxjs/operators';

@Injectable()
export class InstallationConfigurationService extends BaseComponent {

	constructor(private http: BaseHttpService) { super(); }

	private _version: string;
	get version(): string {
		return this._version;
	}

	private _defaultTheme: ThemeType;
	get defaultTheme(): ThemeType {
		return this._defaultTheme || ThemeType.Blue;
	}

	private _defaultLanguage: LanguageType;
	get defaultLanguage(): LanguageType {
		return this._defaultLanguage || LanguageType.English;
	}

	private _defaultCulture: string;
	get defaultCulture(): string {
		return this._defaultCulture || 'en-US';
	}

	private _defaultTimezone: string;
	get defaultTimezone(): string {
		return this._defaultTimezone || 'UTC';
	}

	private _idpServiceAddress: string;
	get idpServiceAddress(): string {
		return this._idpServiceAddress || './';
	}

	private _notificationServiceAddress: string;
	get notificationServiceAddress(): string {
		return this._notificationServiceAddress || './';
	}

	private _appServiceAddress: string;
	get appServiceAddress(): string {
		return this._appServiceAddress || './';
	}

	private _userServiceAddress: string;
	get userServiceAddress(): string {
		return this._userServiceAddress || './';
	}

	private _authClientId: string;
	get authClientId(): string {
		return this._authClientId;
	}

	private _authScope: string;
	get authScope(): string {
		return this._authScope;
	}

	private _authRealm: string;
	get authRealm(): string {
		return this._authRealm;
	}

	private _authFlow: KeycloakFlow;
	get authFlow(): KeycloakFlow {
		return this._authFlow;
	}

	private _authRedirectUri: string;
	get authRedirectUri(): string {
		return this._authRedirectUri;
	}

	private _authLogoutUri: string;
	get authLogoutUri(): string {
		return this._authLogoutUri;
	}

	private _userSettingsVersion: string;
	get userSettingsVersion(): string {
		return this._userSettingsVersion;
	}

	private _idpServiceEnabled: boolean;
	get idpServiceEnabled(): boolean {
		return this._idpServiceEnabled;
	}

	private _notificationServiceEnabled: boolean;
	get notificationServiceEnabled(): boolean {
		return this._notificationServiceEnabled;
	}

	private _appServiceEnabled: boolean;
	get appServiceEnabled(): boolean {
		return this._appServiceEnabled;
	}

	private _userServiceEnabled: boolean;
	get userServiceEnabled(): boolean {
		return this._userServiceEnabled;
	}

	private _logging: boolean;
	get logging(): boolean {
		return this._logging;
	}

	private _logLevels: LogLevel[];
	get logLevels(): LogLevel[] {
		return this._logLevels;
	}

	private _globalErrorHandlingTransmitLogs: boolean;
	get globalErrorHandlingTransmitLogs(): boolean {
		return this._globalErrorHandlingTransmitLogs;
	}

	private _globalErrorHandlingAppName: string;
	get globalErrorHandlingAppName(): string {
		return this._globalErrorHandlingAppName;
	}

	private _inAppNotificationsCountInterval: number;
	get inAppNotificationsCountInterval(): number {
		return this._inAppNotificationsCountInterval || 3200;
	}

	private _fundingCallCorpus: string;
	get fundingCallCorpus(): string {
		return this._fundingCallCorpus;
	}

	private _fundingCallModel: string;
	get fundingCallModel(): string {
		return this._fundingCallModel;
	}

	private _getCallsSimilarToResearcherCollectionName: string;
	get getCallsSimilarToResearcherCollectionName(): string {
		return this._getCallsSimilarToResearcherCollectionName;
	}

	private _getMetdataDocByIdCorpusCollection: string;
	get getMetdataDocByIdCorpusCollection(): string {
		return this._getMetdataDocByIdCorpusCollection;
	}

	private _getThetasResearcherByIDForChart1: string;
	get getThetasResearcherByIDForChart1(): string {
		return this._getThetasResearcherByIDForChart1;
	}

	private _getThetasResearcherByIDForChart1Model: string;
	get getThetasResearcherByIDForChart1Model(): string {
		return this._getThetasResearcherByIDForChart1Model;
	}

	private _getThetasResearcherByIDForChart2: string;
	get getThetasResearcherByIDForChart2(): string {
		return this._getThetasResearcherByIDForChart2;
	}

	private _getThetasResearcherByIDForChart2Model: string;
	get getThetasResearcherByIDForChart2Model(): string {
		return this._getThetasResearcherByIDForChart2Model;
	}

	private _totalResearchersDisplayed: number;
	get getTotalResearchersDisplayed(): number {
		return this._totalResearchersDisplayed;
	}

	private _researchersDisplayedPerPage: number;
	get getResearchersDisplayedPerPage(): number {
		return this._researchersDisplayedPerPage;
	}

	private _totalRGsDisplayed: number;
	get getTotalRGsDisplayed(): number {
		return this._totalRGsDisplayed;
	}

	private _rgsDisplayedPerPage: number;
	get getRGsDisplayedPerPage(): number {
		return this._rgsDisplayedPerPage;
	}

	private _totalCallsDisplayed: number;
	get getTotalCallsDisplayed(): number {
		return this._totalCallsDisplayed;
	}

	private _callsDisplayedPerPage: number;
	get getCallsDisplayedPerPage(): number {
		return this._callsDisplayedPerPage;
	}

	private _knowledgeMapTotalResearchersDisplayed: number;
	get getKnowledgeMapTotalResearchersDisplayed(): number {
		return this._knowledgeMapTotalResearchersDisplayed;
	}

	private _knowledgeMapResearchersDisplayedPerPage: number;
	get getKnowledgeMapResearchersDisplayedPerPage(): number {
		return this._knowledgeMapResearchersDisplayedPerPage;
	}

	private _knowledgeMapTotalRGsDisplayed: number;
	get getKnowledgeMapTotalRGsDisplayed(): number {
		return this._knowledgeMapTotalRGsDisplayed;
	}

	private _knowledgeMapRGsDisplayedPerPage: number;
	get getKnowledgeMapRGsDisplayedPerPage(): number {
		return this._knowledgeMapRGsDisplayedPerPage;
	}

	loadInstallationConfiguration(): Promise<any> {
		return new Promise((r, e) => {

			// We need to exclude all interceptors here, for the initial configuration request.
			const params = new BaseHttpParams();
			params.interceptorContext = {
				excludedInterceptors: [
				InterceptorType.AuthToken,
				InterceptorType.TenantHeaderInterceptor,
				InterceptorType.JSONContentType,
				InterceptorType.Locale,
				InterceptorType.ProgressIndication,
				InterceptorType.RequestTiming,
				InterceptorType.UnauthorizedResponse,
				InterceptorType.ErrorHandlerInterceptor]
			};

			this.http.get('./assets/config.json', { params: params }).pipe(catchError((err: any, caught: Observable<any>) => throwError(err)))
				.pipe(takeUntil(this._destroyed))
				.subscribe(
					(content: InstallationConfigurationService) => {
						this.parseResponse(content);
						r(this);
					},
					reason => e(reason));
		});
	}

	parseResponse(config: any) {
		this._version = config.version;
		this._defaultTheme = config.defaultTheme;
		this._defaultLanguage = config.defaultLanguage;
		this._defaultCulture = config.defaultCulture;
		this._defaultTimezone = config.defaultTimezone;

		if (config.app_service) {
			this._appServiceEnabled = config.app_service.enabled;
			this._appServiceAddress = config.app_service.address;
		}
		if (config.user_service) {
			this._userServiceEnabled = config.user_service.enabled;
			this._userServiceAddress = config.user_service.address;
		}
		if (config.notification_service) {
			this._notificationServiceEnabled = config.notification_service.enabled;
			this._notificationServiceAddress = config.notification_service.address;
		}

		this._inAppNotificationsCountInterval = config.inAppNotificationsCountInterval;

		if (config.idp_service) {
			this._idpServiceEnabled = config.idp_service.enabled;
			this._idpServiceAddress = config.idp_service.address;
			this._authClientId = config.idp_service.clientId;
			this._authScope = config.idp_service.scope;
			this._authRealm = config.idp_service.realm;
			this._authFlow = config.idp_service.flow;
			this._authRedirectUri = config.idp_service.redirectUri;
			this._authLogoutUri = this._idpServiceAddress +
				 '/realms/' +
				 this._authRealm +
				 '/protocol/openid-connect/logout?redirect_uri=' +
				 encodeURIComponent(this._authRedirectUri)
		}

		this._logging = config.logging.enabled;
		this._logLevels = config.logging.logLevels;

		this._globalErrorHandlingTransmitLogs = config.globalErrorHandling.transmitLogs;
		this._globalErrorHandlingAppName = config.globalErrorHandling.appName;

		this._userSettingsVersion = config.userSettingsVersion;

		if (config.recommendationTool) {
			this._fundingCallCorpus = config.recommendationTool.fundingCallCorpus;
			this._fundingCallModel = config.recommendationTool.fundingCallModel;
			this._getCallsSimilarToResearcherCollectionName = config.recommendationTool.getCallsSimilarToResearcherCollectionName;
			this._getMetdataDocByIdCorpusCollection = config.recommendationTool.getMetdataDocByIdCorpusCollection;
			this._getThetasResearcherByIDForChart1 = config.recommendationTool.getThetasResearcherByIDForChart1;
			this._getThetasResearcherByIDForChart1Model = config.recommendationTool.getThetasResearcherByIDForChart1Model;
			this._getThetasResearcherByIDForChart2 = config.recommendationTool.getThetasResearcherByIDForChart2;
			this._getThetasResearcherByIDForChart2Model = config.recommendationTool.getThetasResearcherByIDForChart2Model;
			this._totalResearchersDisplayed = config.recommendationTool.totalResearchersDisplayed;
			this._researchersDisplayedPerPage = config.recommendationTool.researchersDisplayedPerPage;
			this._totalRGsDisplayed = config.recommendationTool.totalRGsDisplayed;
			this._rgsDisplayedPerPage = config.recommendationTool.rgsDisplayedPerPage;
			this._totalCallsDisplayed = config.recommendationTool.totalCallsDisplayed;
			this._callsDisplayedPerPage = config.recommendationTool.callsDisplayedPerPage;
		}

		if (config.knowledgeMap) {
			this._knowledgeMapTotalResearchersDisplayed = config.knowledgeMap.totalResearchersDisplayed;
			this._knowledgeMapResearchersDisplayedPerPage = config.knowledgeMap.researchersDisplayedPerPage;
			this._knowledgeMapTotalRGsDisplayed = config.knowledgeMap.totalRGsDisplayed;
			this._knowledgeMapRGsDisplayedPerPage = config.knowledgeMap.rgsDisplayedPerPage;
		}

	}
}
