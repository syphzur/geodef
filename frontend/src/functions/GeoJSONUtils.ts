import { SpatialObject } from '@/models/SpatialObject';
import { Feature, FeatureCollection } from 'geojson';

export class GeoJSONUtils {
  static toSpatialObjects(fc: FeatureCollection): Array<SpatialObject> {
    return fc.features.map((f) => this.toSpatialObject(f));
  }

  private static toSpatialObject(feature: Feature): SpatialObject {
    const obj = feature.properties as SpatialObject;
    obj.geometry = feature.geometry;
    return obj;
  }
}
