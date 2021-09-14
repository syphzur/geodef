import { GenericHttpService } from './GenericHttpService';
import { SpatialObject } from '@/models/SpatialObject';
import { AxiosResponse } from 'axios';

class SpatialObjectService extends GenericHttpService<SpatialObject> {
  constructor() {
    super('spatial-object/');
  }

  getAllByLayer(
    layerUuid: string,
    withHierarchicalLayers: boolean
  ): Promise<AxiosResponse<Array<SpatialObject>>> {
    return this.http.get<Array<SpatialObject>>(this.url + 'all', {
      params: {
        layerUuid: layerUuid,
        withHierarchicalLayers: withHierarchicalLayers
      }
    });
  }

  getAllByType(typeUuid: string): Promise<AxiosResponse<Array<SpatialObject>>> {
    return this.http.get<Array<SpatialObject>>(this.url + 'all', {
      params: {
        typeUuid: typeUuid
      }
    });
  }

  getAllByCreator(
    creatorUuid: string
  ): Promise<AxiosResponse<Array<SpatialObject>>> {
    return this.http.get<Array<SpatialObject>>(this.url + 'all', {
      params: { creatorUuid: creatorUuid }
    });
  }

  postUpdated(
    arr: Array<SpatialObject>,
    userUuid: string
  ): Promise<AxiosResponse<void>> {
    return this.http.post(this.url + 'all-update', arr, {
      params: { userUuid: userUuid }
    });
  }
}

export default new SpatialObjectService();
