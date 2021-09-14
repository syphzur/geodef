import { Entity } from './Entity';
import { Role } from './Role';

export class User implements Entity {
  pictureUrl?: string;
  familyName?: string;
  givenName?: string;

  constructor(
    public uuid?: string,
    public username?: string,
    public email?: string,
    public role?: Role
  ) {}

  get name(): string {
    return this.username ?? '';
  }
}
