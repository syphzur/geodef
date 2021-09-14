import { GpsTracker } from '@/models/GpsTracker';
import { AxiosResponse } from 'axios';
import { GenericHttpService } from './GenericHttpService';

class GpsTrackerService extends GenericHttpService<GpsTracker> {
  constructor() {
    super('gps-tracker/');
  }

  getAllByOwner(ownerUuid: string): Promise<AxiosResponse<Array<GpsTracker>>> {
    return this.http.get<Array<GpsTracker>>(this.url + 'all', {
      params: { ownerUuid: ownerUuid }
    });
  }

  putForUser(uuid: string, data: GpsTracker): Promise<AxiosResponse> {
    return this.http.put(this.url + 'user/' + uuid, data);
  }
}

export default new GpsTrackerService();
