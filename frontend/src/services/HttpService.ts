import { Messages } from '@/models/constants/Messages';
import axios, { AxiosInstance } from 'axios';
import notificationService from './NotificationService';

export class HttpService {
  protected http: AxiosInstance;
  protected url: string;

  constructor(url: string) {
    this.url = process.env.VUE_APP_SERVER_ADDRESS + url;
    this.http = axios.create({
      baseURL: this.url,
      withCredentials: true
    });
    this.http.interceptors.response.use(
      (resp) => {
        return resp;
      },
      (err) => {
        if (err.response) {
          const data = err.response.data;
          if (Array.isArray(data)) {
            data.forEach((item) => {
              notificationService.showError(item.message);
            });
          } else {
            notificationService.showError(data.message);
          }
        } else {
          notificationService.showError(Messages.NO_CONNECTION);
        }
      }
    );
  }
}
