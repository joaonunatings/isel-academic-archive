import {Component, OnInit} from '@angular/core';
import {MsalService} from "@azure/msal-angular";
import {HttpClient} from "@angular/common/http";
import {AuthenticationResult} from "@azure/msal-browser";
import {Utils} from "../../utils/utils";
import {MyErrorHandler} from "../../services/my-error-handler";

@Component({
  selector: 'app-login-info',
  templateUrl: './login-info.component.html',
  styleUrls: ['./login-info.component.css']
})
export class LoginInfoComponent implements OnInit {

	hasLoggedIn : boolean = false
	image       : any
	name        : string | undefined = ''
	username    : string | undefined = ''

	private authResult : AuthenticationResult | undefined

	constructor(private authService: MsalService,private errorHandler: MyErrorHandler, private http: HttpClient) { }

	ngOnInit(): void {
	}

	login() : void {
		this.authService.loginPopup({scopes: ['https://graph.microsoft.com/.default'], prompt: 'consent'})
			.subscribe({
				next: (result) => {
					this.authResult = result
					this.getUserProfile()
				},
				error: (error) => {
					this.errorHandler.handleError(error)
					this.hasLoggedIn = false
				}
			});
	}

	logout() : void {
		this.authService.logout()
			.subscribe({
				next: (res) => {
					this.name = undefined
					this.hideUserOptions()
					Utils.getElementAndSetVisibility('profile','hidden','0')
				},
				error: (error) => {
					this.errorHandler.handleError(error)
					this.hasLoggedIn = false
				}
			})
	}

	getUserProfile() : void {
		this.hasLoggedIn = true
		this.setUserInfo()
	}

	setUserInfo() : void {
		this.name = this.authResult?.account?.name
		this.username = this.authResult?.account?.username
		Utils.getElementAndSetVisibility('profile','visible','1')
	}

	toggledOptions : boolean = false
	toggleUserOptions() : void {
		let visibility = 'hidden', opacity = '0'
		if(!this.toggledOptions) {
			visibility = 'visible'
			opacity = '1'
			this.toggledOptions = true
		} else {
			this.toggledOptions = false
		}
		Utils.getElementAndSetVisibility('user-options',visibility,opacity)
	}

	showUserOptions() : void {
		Utils.getElementAndSetVisibility('user-options','visible','1')
	}

	hideUserOptions() : void {
		Utils.getElementAndSetVisibility('user-options','hidden','0')
	}

}
