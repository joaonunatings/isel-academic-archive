import { Injectable } from '@angular/core';
import {Subject} from "rxjs";
import {Alert, AlertType} from "../components/alert/alert.component";

@Injectable({
  providedIn: 'root'
})
export class AlertService {

	alertSource = new Subject<Alert>()

	/**
	 * Observable subscribed by alert component
	 */
	alertToBeShown$ = this.alertSource.asObservable()

	constructor() { }

	/**
	 * Sends a new alert, to be shown, to the observable source, and consequently to the alert component
	 * @param alert Alert object to be sent, with defined type, subject and text
	 */
	showAlert(alert: Alert) : void {
		this.alertSource.next(alert)
	}

	/**
	 * Creates and sends a new alert object to the observable source.
	 * @param type	Alert type
	 * @param message	Alert text message
	 * @param subject	Alert subject header
	 */
	newAlert(type: AlertType, message?: string, subject?: string) : void {
		const alert = new Alert()
		let msg = ''
		let sbj = ''
		if(message) msg = message
		if(subject) sbj = subject
		alert.type = type
		alert.subject = sbj
		alert.text = msg
		this.alertSource.next(alert)
	}
}
