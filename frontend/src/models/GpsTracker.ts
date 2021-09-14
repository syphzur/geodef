import { Type } from 'class-transformer';
import { Entity } from './Entity';
import 'reflect-metadata';

export class GpsTracker implements Entity {
  @Type(() => Date)
  leatestDataDateTime?: Date;

  constructor(
    public uuid?: string,
    public imei?: string,
    public ownerUuid?: string
  ) {}

  get name(): string {
    return this.imei ?? '';
  }
}
