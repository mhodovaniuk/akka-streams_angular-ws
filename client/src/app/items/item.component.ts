import {Component, OnInit} from '@angular/core';
import {ItemService, Item} from '../item.service'
import {AuthService} from '../auth.service'

@Component({
  selector: 'app-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.css']
})
export class ItemComponent implements OnInit {
  private _authService: AuthService;
  private _itemService: ItemService;
  private _item: Item = new Item(null, null);
  private _position: number = -1;

  constructor(authService: AuthService, itemService: ItemService) {
    this._authService = authService;
    this._itemService = itemService;
  }


  get item(): Item {
    return this._item;
  }

  set item(item: Item) {
    this._item = item;
  }

  get position(): number {
    return this._position;
  }

  set position(position: number) {
    this._position = position;
  }

  get authService(): AuthService {
    return this._authService;
  }

  get itemService(): ItemService {
    return this._itemService;
  }

  ngOnInit(): void {
  }

  cancel() {
    this._item = new Item(null, null);
    this.position = -1;
  }

  edit(posititon: number, item: Item) {
    this._position = posititon;
    this._item = new Item(item.id, item.name);
  }

  createOrUpdate() {
    if (this._item.id)
      this._itemService.update(this.position, this._item);
    else
      this._itemService.create(this.position, this._item);
  }

  remove(item: Item) {
    this._itemService.remove(item);
  }

}
