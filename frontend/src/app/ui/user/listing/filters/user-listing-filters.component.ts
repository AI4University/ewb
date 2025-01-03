import { Component, computed, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { AppRole } from '@app/core/enum/app-role';
import { IsActive } from '@app/core/enum/is-active.enum';
import { UserRoleLookup } from '@idp-service/core/query/user-role.lookup';
import { UserFilter } from '@idp-service/core/query/user.lookup';
import { AppEnumUtils } from '@app/core/formatting/enum-utils.service';
import { BaseComponent } from '@common/base/base.component';
import { nameof } from 'ts-simple-nameof';

@Component({
	selector: 'app-user-listing-filters',
	templateUrl: './user-listing-filters.component.html',
	styleUrls: ['./user-listing-filters.component.scss']
})
export class UserListingFiltersComponent extends BaseComponent implements OnInit, OnChanges {

	@Input() readonly filter: UserFilter;
	@Output() filterChange = new EventEmitter<UserFilter>();
	appRole = AppRole;

	roles: string[] = []
	appRoleEnumValues = this.enumUtils.getEnumValues<AppRole>(AppRole);

	// * State
	internalFilters: UserListingFilters = this._getEmptyFilters();

	protected appliedFilterCount: number = 0;
	constructor(
		public enumUtils: AppEnumUtils,
	) { super(); }

	ngOnInit() {
	}

	ngOnChanges(changes: SimpleChanges): void {
		const filterChange = changes[nameof<UserListingFiltersComponent>(x => x.filter)]?.currentValue as UserFilter;
		if (filterChange) {
			this.updateFilters()
		}
	}


	onSearchTermChange(searchTerm: string): void {
		this.applyFilters()
	}


	protected updateFilters(): void {
		this.internalFilters = this._parseToInternalFilters(this.filter);
		this.appliedFilterCount = this._computeAppliedFilters(this.internalFilters);
	}

	protected applyFilters(): void {
		const { isActive, like, userRoleSubQuery } = this.internalFilters ?? {};
		let filter = {
			...this.filter,
			like,
			isActive: isActive ? [IsActive.Active] : [IsActive.Inactive],
            userRoleSubQuery
		}
		this.filterChange.emit(filter);
	}


	private _parseToInternalFilters(inputFilter: UserFilter): UserListingFilters {
		if (!inputFilter) {
			return this._getEmptyFilters();
		}

		let { excludedIds, ids, isActive, like, userRoleSubQuery } = inputFilter;

		return {
			isActive: (isActive ?? [])?.includes(IsActive.Active) || !isActive?.length,
			like: like,
			userRoleSubQuery: userRoleSubQuery ?? new UserRoleLookup()
		}

	}

	private _getEmptyFilters(): UserListingFilters {
		return {
			isActive: true,
			like: null,
			userRoleSubQuery: new UserRoleLookup()
		}
	}

	private _computeAppliedFilters(filters: UserListingFilters): number {
		let count = 0;
		if (!filters?.isActive) {
			count++
		}
		if (filters?.like) {
			count++;
		}
		if (filters?.userRoleSubQuery?.roles?.length) {
			count++;
		}

		return count;
	}

	clearFilters() {
		this.internalFilters = this._getEmptyFilters();
        this.appliedFilterCount = this._computeAppliedFilters(this.internalFilters);
	}
}

interface UserListingFilters {
	isActive: boolean;
	like: string;
	userRoleSubQuery: UserRoleLookup;
}
