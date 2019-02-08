import { User } from '../user.model';

export class UserDTO {
  public id: string;
  public type: string;
  public email: string;
  public password: string;

  constructor(obj: Object = {}) {
    Object.assign(this, obj);
  }

  static toModel(u: UserDTO): User {
    return <User>{
      id: u.id,
      type: u.type,
      email: u.email,
      password: u.password
    };
  }
}
