import { Page } from '@/models/Page';
import { AxiosResponse } from 'axios';
import { DataOptions } from 'vuetify';

export interface HttpServiceWithUserEndpoints<T> {
  getSingle(uuid: string): Promise<AxiosResponse>;
  getAll(): Promise<AxiosResponse<Array<T>>>;
  getPage(pageInfo: DataOptions): Promise<AxiosResponse<Page<T>>>;
  post(data: T): Promise<AxiosResponse>;
  put(uuid: string, data: T): Promise<AxiosResponse>;
  delete(uuid: string): Promise<AxiosResponse>;
  putForUser(uuid: string, data: any): Promise<AxiosResponse>;
  deleteForUser(uuid: string, creatorUuid: string): Promise<AxiosResponse>;
}
