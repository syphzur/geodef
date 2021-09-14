import store from '@/store';
import { Module, VuexModule, Mutation, Action } from 'vuex-module-decorators';
import { User } from '@/models/User';
import notificationService from '@/services/NotificationService';
import authService from '@/services/AuthService';
import { Role } from '@/models/Role';

@Module({ namespaced: true, name: 'AuthModule', dynamic: true, store })
export default class AuthModule extends VuexModule {
  private _isSignedIn = false;
  private _profile: User | null = null;
  private _token: string | null = null;

  get isSignedIn(): boolean {
    return this._isSignedIn;
  }

  get isAdmin(): boolean {
    return this._isSignedIn && this._profile?.role === Role.ADMIN;
  }

  get profile(): User {
    return this._profile as User;
  }

  get token(): string {
    return this._token as string;
  }

  @Mutation
  setIsSignedIn(value: boolean) {
    this._isSignedIn = value;
  }

  @Mutation
  setProfile(value: User) {
    this._profile = value;
  }

  @Mutation
  onSignOut() {
    this._profile = null;
    this._token = null;
  }

  @Mutation
  setToken(value: string) {
    this._token = value;
  }

  @Action
  init() {
    authService.init().then(async () => {
      const user = authService.auth.currentUser;
      const idToken = user.get().getAuthResponse().id_token;
      if (idToken) {
        this.setToken(idToken);
        const resp = await authService.getUserProfile(idToken);
        if (resp) {
          this.setUserInfo(resp.data);
        }
      }
      // listener is called by values from cookies and also 5 mins before token expire date
      authService.auth.currentUser.listen((GUser) => {
        const idToken = GUser.getAuthResponse().id_token;
        if (idToken) {
          this.setToken(idToken);
          authService.getUserProfile(idToken).then((resp) => {
            this.setUserInfo(resp.data);
          });
        }
      });
    });
  }

  @Action
  signIn() {
    authService
      .signIn()
      .then((accessToken) => this.exchangeCodeForToken(accessToken.code));
  }

  @Action
  signOut() {
    authService.signOut();
    this.onSignOut();
    this.setIsSignedIn(false);
    notificationService.showSuccess('Logged out successfully.');
  }

  @Action
  setUserInfo(profile: User) {
    this.setIsSignedIn(true);
    this.setProfile(profile);
    notificationService.showSuccess('Welcome ' + this.profile?.username + '!');
  }

  @Action
  async exchangeCodeForToken(oneTimeCode: string) {
    const resp = await authService.exchangeCodeForToken(oneTimeCode);
    this.setToken(resp.headers['authorization']);
  }
}
