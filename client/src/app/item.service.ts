import {Injectable} from '@angular/core';
import {WSService, WSMessage} from "./ws.service";

export class Item {
  public id: number;
  public name: string;

  constructor(id: number, name: string) {
    this.id = id;
    this.name = name;
  }

}

interface ItemAddedUpdatedMessage extends WSMessage {
  position: number,
  item: Item
}

interface ItemUpdatedMessage extends WSMessage {
  item: Item
}

interface ItemRemoveMessage extends WSMessage {
  id: number
}

interface SubscribeMessage extends WSMessage {
  items: Array<Item>
}

@Injectable()
export class ItemService {
  private _items: Array<Item> = [];
  private _wsService: WSService;
  private _subscribed: boolean = false;

  constructor(wsService: WSService) {
    this._wsService = wsService;
    this._wsService.subscribe(this.next.bind(this));
  }

  public next(wsMessage) {
    if (wsMessage.$type == "item_added_updated") {
      let message = <ItemAddedUpdatedMessage> wsMessage;
      let oldIndex = ItemService.indexOfItemById(this._items, message.item.id);
      if (oldIndex == -1) {
        this._items.splice(message.position, 0, message.item);
      } else {
        this._items.splice(message.position, 1, message.item);
      }
    } else if (wsMessage.$type == "item_removed") {
      let message = <ItemRemoveMessage> wsMessage;
      let index = ItemService.indexOfItemById(this._items, message.id);
      if (index != -1) {
        this._items.splice(index, 1);
      }
    } else if (wsMessage.$type == "subscribe_items") {
      let message = <SubscribeMessage> wsMessage;
      this._items = message.items;
    }
  }

  private static indexOfItemById(items: Array<Item>, id: number) {
    let found = false;
    let i = 0;
    while (i < items.length && !found) {
      found = items[i].id == id;
      i++;
    }
    if (found)
      return i - 1;
    else
      return -1;
  }


  public get items(): Array<Item> {
    return this._items;
  }


  public get isSubscribed(): boolean {
    return this._subscribed;
  }

  public subscribe() {
    this._wsService.subscribeItems();
    this._subscribed = true;
  }

  public unsubscribe() {
    this._wsService.unsubscribeItems();
    this._subscribed = false;
  }

  public remove(item: Item) {
    this._wsService.remove(item);
  }

  public update(position:number, item: Item) {
    if (!this.isSubscribed)
      this.subscribe();
    this._wsService.update(position, item);
  }

  public create(position: number, item: Item) {
    if (!this.isSubscribed)
      this.subscribe();
    this._wsService.create(position, item);
  }
}
