import { Injectable } from '@angular/core';
import { InstallationConfigurationService } from '@common/installation-configuration/installation-configuration.service';
import { BaseHttpService } from '@common/base/base-http.service';
import { Observable, throwError } from 'rxjs';
import { UserLookup } from '@idp-service/core/query/user.lookup';
import { QueryResult } from '@common/model/query-result';
import { User, UserPersist, UserRolePatchPersist } from '@app/core/model/user/user.model';
import { catchError } from 'rxjs/operators';
import { Guid } from '@common/types/guid';

@Injectable()
export class UserService {

	private get apiBase(): string { return `${this.installationConfiguration.appServiceAddress}api/user`; }

	constructor(
		private installationConfiguration: InstallationConfigurationService,
		private http: BaseHttpService
	) { }

	query(q: UserLookup): Observable<QueryResult<User>> {
		const url = `${this.apiBase}/query`;
		return this.http.post<QueryResult<User>>(url, q).pipe(catchError((error: any) => throwError(error)));
	}

	getSingle(id: Guid, reqFields: string[] = []): Observable<User> {
		const url = `${this.apiBase}/${id}`;
		const options = { params: { f: reqFields } };

		return this.http
			.get<User>(url, options).pipe(
				catchError((error: any) => throwError(error)));
	}

	persist(item: UserPersist): Observable<User> {
		const url = `${this.apiBase}/persist`;

		return this.http
			.post<User>(url, item).pipe(
				catchError((error: any) => throwError(error)));
	}

	persistRoles(item: UserRolePatchPersist): Observable<User> {
		const url = `${this.apiBase}/persist/roles`;

		return this.http
			.post<User>(url, item).pipe(
				catchError((error: any) => throwError(error)));
	}

	delete(id: Guid): Observable<User> {
		const url = `${this.apiBase}/${id}`;

		return this.http
			.delete<User>(url).pipe(
				catchError((error: any) => throwError(error)));
	}

}
