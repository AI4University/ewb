import { Lookup } from '@common/model/lookup';
import { Guid } from '@common/types/guid';
import { IsActive } from '@idp-service/core/enum/is-active.enum';
import { UserRoleLookup } from './user-role.lookup';

export class UserLookup extends Lookup implements UserFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	userRoleSubQuery: UserRoleLookup;

	constructor() {
		super();
	}
}

export interface UserFilter {
	ids: Guid[];
	excludedIds: Guid[];
	like: string;
	isActive: IsActive[];
	userRoleSubQuery: UserRoleLookup;
}
