import {Injectable} from '@angular/core';
import {WSService, WSMessage} from "./ws.service";

interface PongResponse extends WSMessage {
  seq: number
}

@Injectable()
export class PingService {
  private readonly POLLING_INTERVAL: number = 1000;
  private readonly DISCONNECT_THRESHOLD: number = 20000;
  private _wsService: WSService;
  private _seq: number = 0;
  private _calls: Map<number, number> = new Map<number, number>();
  private _ping: number = null;
  private _lastResponseTimestamp: number = null;

  constructor(wsService: WSService) {
    this._wsService = wsService;
    this._wsService.subscribe(this.next.bind(this));
    setInterval(this.sendPing.bind(this), this.POLLING_INTERVAL)
  }

  public next(wsMessage: WSMessage) {
    if (wsMessage.$type == 'pong') {
      let message = <PongResponse> wsMessage;
      if (this._calls.has(message.seq)) {
        this._lastResponseTimestamp = new Date().getMilliseconds();
        this._ping = new Date().getMilliseconds() - this._calls.get(message.seq);
        this._calls.delete(message.seq);
      }
    }
  }

  private sendPing() {
    if (this._calls.size > 100)
      this._calls.clear();
    this._seq++;
    this._wsService.ping(this._seq);
    this._calls.set(this._seq, new Date().getMilliseconds());
  }

  get ping() {
    if (new Date().getMilliseconds() - this._lastResponseTimestamp > this.DISCONNECT_THRESHOLD)
      return null;
    else
      return this._ping;
  }
}
