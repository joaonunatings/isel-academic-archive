import {Component, ViewChild} from '@angular/core';
import {MsalService} from "@azure/msal-angular";
import {NavBarComponent} from "./components/nav-bar/nav-bar.component";

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {
  	title = 'TSM - Team Shifts Management'

	constructor() {	}
}
