import { GenericHttpService } from './GenericHttpService';
import { FeatureCollection } from 'geojson';
import { AxiosResponse } from 'axios';

class GeoJSONService extends GenericHttpService<FeatureCollection> {
  constructor() {
    super('geojson/');
  }

  getAllGpsData(): Promise<AxiosResponse<FeatureCollection>> {
    return this.http.get<FeatureCollection>(this.url + 'gps-data/all');
  }

  getAllSpatialObjects(): Promise<AxiosResponse<FeatureCollection>> {
    return this.http.get<FeatureCollection>(this.url + 'spatial-object/all');
  }

  getAllByLayer(
    layerUuid: string,
    withHierarchicalLayers: boolean
  ): Promise<AxiosResponse<FeatureCollection>> {
    return this.http.get<FeatureCollection>(this.url + 'spatial-object/all', {
      params: {
        layerUuid: layerUuid,
        withHierarchicalLayers: withHierarchicalLayers
      }
    });
  }

  getAllByType(typeUuid: string): Promise<AxiosResponse<FeatureCollection>> {
    return this.http.get<FeatureCollection>(this.url + 'spatial-object/all', {
      params: {
        typeUuid: typeUuid
      }
    });
  }

  getAllByCreator(
    creatorUuid: string
  ): Promise<AxiosResponse<FeatureCollection>> {
    return this.http.get<FeatureCollection>(this.url + 'spatial-object/all', {
      params: {
        creatorUuid: creatorUuid
      }
    });
  }

  getAllByUser(
    userUuid: string,
    dateFrom?: string,
    dateTo?: string,
    trackerUuid?: string
  ): Promise<AxiosResponse<FeatureCollection>> {
    return this.http.get<FeatureCollection>(this.url + 'gps-data/all', {
      params: {
        userUuid: userUuid,
        dateFrom: dateFrom,
        dateTo: dateTo,
        trackerUuid: trackerUuid
      }
    });
  }
}

export default new GeoJSONService();
