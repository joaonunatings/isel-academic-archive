import {Component, Input, OnInit} from '@angular/core';
import {AuthenticationResult} from "@azure/msal-browser";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {

	constructor() { }

	ngOnInit(): void { }

}
