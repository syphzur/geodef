import { Type } from 'class-transformer/decorators';
import { Point } from 'geojson';
import { Entity } from './Entity';
import 'reflect-metadata';

export class GpsData implements Entity {
  trackerImei?: string;

  @Type(() => Date)
  dateTime?: Date;

  constructor(
    public uuid: string,
    public point: Point,
    public speed: number,
    public trackerUuid: string
  ) {}

  get name(): string {
    return this.dateTime
      ? 'Data from ' + this.dateTime.toLocaleString()
      : this.uuid;
  }
}
