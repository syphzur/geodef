import { GpsData } from '@/models/GpsData';
import { Page } from '@/models/Page';
import { AxiosResponse } from 'axios';
import { DataOptions } from 'vuetify';
import { GenericHttpService } from './GenericHttpService';

class GpsDataService extends GenericHttpService<GpsData> {
  constructor() {
    super('gps-data/');
  }

  getPageByUser(
    userUuid: string,
    pageInfo: DataOptions,
    dateFrom?: string,
    dateTo?: string,
    trackerUuid?: string
  ): Promise<AxiosResponse<Page<GpsData>>> {
    const sort =
      pageInfo.sortBy?.length > 0
        ? pageInfo.sortBy[0] + ',' + (pageInfo.sortDesc[0] ? 'desc' : 'asc')
        : null;
    return this.http.get<Page<GpsData>>(this.url + 'all', {
      params: {
        userUuid: userUuid,
        page: pageInfo.page,
        size: pageInfo.itemsPerPage,
        sort: sort,
        dateFrom: dateFrom,
        dateTo: dateTo,
        trackerUuid: trackerUuid
      }
    });
  }

  deleteForUser(uuid: string, creatorUuid: string): Promise<AxiosResponse> {
    return this.http.delete(this.url + 'user/' + uuid, {
      params: { creatorUuid: creatorUuid }
    });
  }
}

export default new GpsDataService();
