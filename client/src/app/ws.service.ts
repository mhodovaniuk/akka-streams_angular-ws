import {Injectable} from '@angular/core';
import {Observer, Observable, Subject} from 'rxjs/Rx';
import {PartialObserver} from "rxjs/Observer";
import {Item} from "./item.service";
import {environment} from '../environments/environment';

export interface WSMessage {
  $type: string;
}

@Injectable()
export class WSService {
  private _subject: Subject<WSMessage>;

  constructor() {
    this.connect(WSService.constructUrl(environment.WS_PORT));
  }

  private static constructUrl(port: number) {
    return `ws://${window.location.hostname}:${port}/api/ws`;
  }

  private connect(url) {
    let ws = new WebSocket(url);

    let observable = Observable.create(
      (obs: Observer<MessageEvent>) => {
        ws.onmessage = obs.next.bind(obs);
        ws.onerror = obs.error.bind(obs);
        ws.onclose = obs.complete.bind(obs);
        return ws.close.bind(ws);
      })
      .map((messageEvent: MessageEvent) => {
        console.log("Message Received: " + messageEvent.data);
        return JSON.parse(messageEvent.data) as Observer<WSMessage>;
      })
      .share() as Observable<WSMessage>;

    let observer = {
      next: (data: WSMessage) => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify(data));
        }
      }
    } as Observer<WSMessage>;

    this._subject = Subject.create(observer, observable)
  }

  public subscribe(observer: PartialObserver<WSMessage>) {
    this._subject.subscribe(observer);
  }

  public login(username: string, password: string) {
    let loginRequest = {
      $type: "login",
      username: username,
      password: password
    } as WSMessage;
    this._subject.next(loginRequest);
  }

  public subscribeItems() {
    let subscribeRequest = {
      $type: "subscribe_items"
    } as WSMessage;
    this._subject.next(subscribeRequest);
  }

  public unsubscribeItems() {
    let unsubscribeRequest = {
      $type: "unsubscribe_items"
    } as WSMessage;
    this._subject.next(unsubscribeRequest);

  }

  public create(position: number, item: Item) {
    let createRequest = {
      $type: "add_update_item",
      position: position,
      item: item
    } as WSMessage;
    this._subject.next(createRequest);
  }

  public update(position: number, item: Item) {
    let updateRequest = {
      $type: "add_update_item",
      position: position,
      item: item
    } as WSMessage;
    this._subject.next(updateRequest);
  }

  public remove(item: Item) {
    let removeRequest = {
      $type: "remove_item",
      id: item.id
    } as WSMessage;
    this._subject.next(removeRequest);
  }

  public ping(seq: number) {
    let pingRequest = {
      $type: "ping",
      seq: seq
    } as WSMessage;
    this._subject.next(pingRequest);
  }
}
