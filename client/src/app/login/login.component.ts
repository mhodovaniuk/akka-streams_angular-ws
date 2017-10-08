import {Component, OnInit} from '@angular/core';
import {AuthService} from "../auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  private _authService: AuthService;
  private _username: string = "";
  private _password: string = "";

  constructor(authService: AuthService) {
    this._authService = authService;
  }

  get authService(): AuthService {
    return this._authService;
  }

  ngOnInit() {
  }

  logIn() {
    this._authService.logIn(this._username, this._password)
  }
}
