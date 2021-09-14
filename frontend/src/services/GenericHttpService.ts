import { Page } from '@/models/Page';
import store from '@/store';
import AuthModule from '@/store/AuthModule';
import { AxiosResponse } from 'axios';
import { DataOptions } from 'vuetify';
import { getModule } from 'vuex-module-decorators';
import { HttpService } from './HttpService';

export class GenericHttpService<T> extends HttpService {
  private authModule: AuthModule = getModule(AuthModule, store);

  constructor(url: string) {
    super(url);
    this.http.interceptors.request.use((config) => {
      config.headers = {
        Authorization: this.authModule.token ?? ''
      };
      return config;
    });
  }

  getSingle(uuid: string): Promise<AxiosResponse<T>> {
    return this.http.get<T>(this.url + uuid);
  }

  getAll(): Promise<AxiosResponse<Array<T>>> {
    return this.http.get<Array<T>>(this.url + 'all');
  }

  getPage(pageInfo: DataOptions): Promise<AxiosResponse<Page<T>>> {
    const sort =
      pageInfo.sortBy?.length > 0
        ? pageInfo.sortBy[0] + ',' + (pageInfo.sortDesc[0] ? 'desc' : 'asc')
        : null;
    return this.http.get<Page<T>>(this.url, {
      params: {
        page: pageInfo.page,
        size: pageInfo.itemsPerPage,
        sort: sort
      }
    });
  }

  post(data: T): Promise<AxiosResponse> {
    return this.http.post(this.url, data);
  }

  put(uuid: string, data: T): Promise<AxiosResponse> {
    return this.http.put(this.url + uuid, data);
  }

  delete(uuid: string): Promise<AxiosResponse> {
    return this.http.delete(this.url + uuid);
  }
}
