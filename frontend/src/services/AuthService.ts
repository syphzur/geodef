import { User } from '@/models/User';
import { AxiosResponse } from 'axios';
import { HttpService } from './HttpService';

class AuthService extends HttpService {
  private _auth!: gapi.auth2.GoogleAuth;
  private gauthOption = {
    /* eslint-disable @typescript-eslint/camelcase */
    client_id:
      '421359721622-0uqtleiqlau6f0l2nia6f4hm15ije91i.apps.googleusercontent.com',
    scope: 'email profile',
    response_type: 'code'
  };

  constructor() {
    super('user/');
  }

  async init() {
    const load = new Promise((resolve) => gapi.load('auth2', resolve));
    return load.then(async () => {
      return await gapi.auth2.init(this.gauthOption).then((auth) => {
        this._auth = auth;
      });
    });
  }

  signIn(): Promise<{ code: string }> {
    return this._auth.grantOfflineAccess();
  }

  signOut() {
    this._auth.signOut();
  }

  get auth(): gapi.auth2.GoogleAuth {
    return this._auth;
  }

  getUserProfile(token: string): Promise<AxiosResponse<User>> {
    return this.http.get<User>(this.url + 'logged', {
      headers: { Authorization: token }
    });
  }

  exchangeCodeForToken(oneTimeCode: string): Promise<AxiosResponse> {
    return this.http.get<User>(this.url + 'get-token', {
      headers: { 'access-code': oneTimeCode }
    });
  }
}

export default new AuthService();
