import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatRadioModule } from '@angular/material/radio';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AgGridModule } from 'ag-grid-angular';
import { ResizableModule } from 'angular-resizable-element';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HomePageComponent } from './components/home-page/home-page.component';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { IndexComponent } from './components/index/index.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { CalendarFormComponent } from './components/calendar-form/calendar-form.component';
import { MembersComponent } from './components/members/members.component';
import { LoginInfoComponent } from './components/loggin-info/login-info.component'
import { ReportComponent } from './components/report/report.component';
import { MyErrorHandler } from "./services/my-error-handler";
import { MatIconModule} from "@angular/material/icon";
import { MatButtonToggleModule } from "@angular/material/button-toggle";
import { InteractionType, PublicClientApplication } from '@azure/msal-browser';
import { MsalInterceptor, MsalModule } from "@azure/msal-angular";
import { AlertComponent } from './components/alert/alert.component';
import { environment } from '../environments/environment';

const msalConfig = {
  auth: {
    clientId: environment.msalClientId,
    authority: 'https://login.microsoftonline.com/consumers/',
    redirectUri: '/'
  },
  cache: {
    cacheLocation: 'localStorage',
    storeAuthStateInCookie: false,
  }
}

@NgModule({
  declarations: [
    AppComponent,
    NavBarComponent,
    HomePageComponent,
    ErrorPageComponent,
    IndexComponent,
    CalendarComponent,
    CalendarFormComponent,
    MembersComponent,
    ReportComponent,
    LoginInfoComponent,
    AlertComponent,
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        AgGridModule,
        ResizableModule,
        MatGridListModule,
        MatRadioModule,
        MatCardModule,
        MatButtonModule,
        MatInputModule,
        NgbModule,
        ReactiveFormsModule,
        HttpClientModule,
        MatIconModule,
        MatButtonToggleModule,
        MsalModule.forRoot(
			new PublicClientApplication(msalConfig),
			{
				interactionType: InteractionType.Redirect,
				authRequest: {
					scopes: ['https://graph.microsoft.com/.default']
				}
			},
			{
				interactionType: InteractionType.Popup, // MSAL Interceptor Configuration
				protectedResourceMap: new Map([
					['http://localhost:8080/api/v1/graph/sync', [{
						httpMethod: 'POST',
						scopes: ['https://graph.microsoft.com/.default']
					}]]
				])
			})
    ],
  providers: [
	MyErrorHandler,
	{
	  provide: HTTP_INTERCEPTORS,
	  useClass: MsalInterceptor,
	  multi: true
	}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
