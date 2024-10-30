import { ContactInfoType } from '@app/core/enum/contact-info-type.enum';
import { BaseEntity, BaseEntityPersist } from '@common/base/base-entity.model';
import { Guid } from '@common/types/guid';


export interface User extends BaseEntity {
	firstName: string;
	lastName: string;
	timezone: string;
	language: string;
	subjectId: string;
	globalRoles: UserRole[];
	contacts: UserContactInfo[];
}

export interface UserContactInfo {
	id: Guid;
	value: String;
	type: ContactInfoType;
	ordinal: number;
	user: User;
	createdAt: Date;
}

export interface UserRole {
	id: Guid;
	role: String;
	user: User;
	createdAt: Date;
}

// persist

export interface UserPersist extends BaseEntityPersist {
	name: String;
	firstName: string;
	lastName: string;
	timezone: string;
	language: string;
}

export interface UserRolePatchPersist {
	id: Guid;
	roles: String[];
	hash: string;
}
