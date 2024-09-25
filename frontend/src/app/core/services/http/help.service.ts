import { Injectable } from "@angular/core";
import { BaseHttpService } from "@common/base/base-http.service";
import { InstallationConfigurationService } from "@common/installation-configuration/installation-configuration.service";
import { Observable } from "rxjs";

@Injectable({
	providedIn: 'root'
  })
  export class HelpService {

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/help`; }

	constructor(
	  private installationConfiguration: InstallationConfigurationService,
	  private http: BaseHttpService) { }

	getManual(): Observable<any> {
	  return this.http.get<any>(`${this.apiBase}/manual`, {responseType: 'blob', observe: 'response' });
	}

	getFaq(): Observable<any> {
	  return this.http.get<any>(`${this.apiBase}/faq`, {responseType: 'blob', observe: 'response' });
	}

  }
