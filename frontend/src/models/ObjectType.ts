import { Entity } from './Entity';

export class ObjectType implements Entity {
  layerName?: string;
  creatorName?: string;
  objectsNumber?: number;

  constructor(
    public name?: string,
    public uuid?: string,
    public layerUuid?: string,
    public creatorUuid?: string
  ) {}
}
