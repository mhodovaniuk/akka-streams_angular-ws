import {Injectable} from '@angular/core';
import {User} from "./user";
import {WSService, WSMessage} from "./ws.service";
import {NextObserver} from 'rxjs/Observer'
import {ItemService} from "./item.service";

interface LoginResponse extends WSMessage {
  user_type: string
}

@Injectable()
export class AuthService {
  private _user: User;
  private _aboutToLogInUserUsername: string;
  private _wsService: WSService;

  constructor(wsService: WSService) {
    this._wsService = wsService;
    this._wsService.subscribe(this.next.bind(this));
  }

  public get user() {
    return this._user;
  }

  public next(wsMessage: WSMessage) {
    if (wsMessage.$type == "login_successful") {
      let message = <LoginResponse> wsMessage;
      this._user = new User(this._aboutToLogInUserUsername, message.user_type);
      this._aboutToLogInUserUsername = null;
    } else if (wsMessage.$type == "login_failed") {
      this._user = null;
      alert("Wrong username or password!");
      this._aboutToLogInUserUsername = null;
    }

  }

  public logIn(username: string, password: string) {
    this._aboutToLogInUserUsername = username;
    this._wsService.login(username, password)
  }

  public isLoggedIn() {
    return this._user != null
  }

  public isAdmin() {
    return this.isLoggedIn() && this._user.role.toLowerCase() == 'ADMIN'.toLowerCase()
  }

  public isUser() {
    return this.isLoggedIn() && this._user.role.toLowerCase() == 'USER'.toLowerCase()
  }
}
