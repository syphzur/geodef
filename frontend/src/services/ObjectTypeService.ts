import { GenericHttpService } from './GenericHttpService';
import { ObjectType } from '@/models/ObjectType';
import { AxiosResponse } from 'axios';
import { DataOptions } from 'vuetify';

export class ObjectTypeService extends GenericHttpService<ObjectType> {
  constructor() {
    super('object-type/');
  }

  getAllByLayer(layerUuid: string): Promise<AxiosResponse<Array<ObjectType>>> {
    return this.http.get<Array<ObjectType>>(this.url + 'all', {
      params: { layerUuid: layerUuid }
    });
  }

  getPageByCreator(
    creatorUuid: string,
    pageInfo: DataOptions
  ): Promise<AxiosResponse<Array<ObjectType>>> {
    const sort =
      pageInfo.sortBy?.length > 0
        ? pageInfo.sortBy[0] + ',' + (pageInfo.sortDesc[0] ? 'desc' : 'asc')
        : null;
    return this.http.get<Array<ObjectType>>(this.url + 'all', {
      params: {
        creatorUuid: creatorUuid,
        page: pageInfo.page,
        size: pageInfo.itemsPerPage,
        sort: sort
      }
    });
  }

  putForUser(uuid: string, data: ObjectType): Promise<AxiosResponse> {
    return this.http.put(this.url + 'user/' + uuid, data);
  }

  deleteForUser(uuid: string, creatorUuid: string): Promise<AxiosResponse> {
    return this.http.delete(this.url + 'user/' + uuid, {
      params: { creatorUuid: creatorUuid }
    });
  }
}

export default new ObjectTypeService();
