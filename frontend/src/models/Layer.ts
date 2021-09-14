import { Entity } from './Entity';

export class Layer implements Entity {
  prevLayerUuid: string | null = null;
  typesCount?: number;

  constructor(public name?: string, public uuid?: string) {}
}
