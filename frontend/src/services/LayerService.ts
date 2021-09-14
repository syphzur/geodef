import { Layer } from '@/models/Layer';
import { AxiosResponse } from 'axios';
import { GenericHttpService } from './GenericHttpService';

class LayerService extends GenericHttpService<Layer> {
  constructor() {
    super('layer/');
  }

  getAllHierarchical(): Promise<AxiosResponse<Array<Layer>>> {
    return this.http.get<Array<Layer>>(this.url + 'all/hierarchical');
  }
}

export default new LayerService();
