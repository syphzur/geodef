import { Geometry } from 'geojson';
import { Entity } from './Entity';

export class SpatialObject implements Entity {
  typeName?: string;
  creatorUsername?: string;
  layerName?: string;

  constructor(
    public name?: string,
    public uuid?: string,
    public creatorUuid?: string,
    public geometry?: Geometry,
    public typeUuid?: string
  ) {}
}
