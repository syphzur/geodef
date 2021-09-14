import { User } from '@/models/User';
import { GenericHttpService } from './GenericHttpService';

class UserService extends GenericHttpService<User> {
  constructor() {
    super('user/');
  }
}

export default new UserService();
