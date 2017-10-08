import {Component, OnInit} from "@angular/core";
import {WSService} from "./ws.service";
import {AuthService} from "./auth.service";
import {PingService} from "./ping.service";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  private _wsService: WSService;
  private _authService: AuthService;
  private _pingService: PingService;

  constructor(authService: AuthService, pingService:PingService, wsService: WSService) {
    this._authService = authService;
    this._pingService = pingService;
    this._wsService = wsService;

  }


  get authService(): AuthService {
    return this._authService;
  }

  get pingService(): PingService {
    return this._pingService;
  }

  ngOnInit(): void {

  }

}
