import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {RouterModule, Routes} from '@angular/router';

import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {ItemComponent} from './items/item.component';

import {ItemService} from './item.service'
import {AuthService} from './auth.service'
import {WSService} from './ws.service'
import {PingService} from "./ping.service";

const appRoutes: Routes = [
  {path: '', component: AppComponent},
  {path: 'login', component: LoginComponent},
  {path: 'items', component: ItemComponent}
];


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ItemComponent
  ],
  imports: [
    RouterModule.forRoot(appRoutes),
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: [
    AuthService,
    PingService,
    ItemService,
    WSService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
