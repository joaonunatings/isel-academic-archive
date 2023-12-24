import { Component, OnInit } from '@angular/core';
import {Utils} from "../../utils/utils";
import {AlertService} from "../../services/alert.service";

const ALERT_TYPES_CONFIG = [{background: 'rgba(190,49,49,0.94)', icon: 'report'}, {background: 'rgba(245,158,6,0.91)', icon: 'report_problem'},
	{background: 'rgba(121,158,218,0.94)', icon: 'message'}, {background: 'rgba(109,164,41,0.94)', icon: 'check_box'}]

export enum AlertType {
	SEVERE,
	WARNING,
	NOTIFICATION,
	CONFIRMATION
}

export class Alert {
	private _type            : AlertType = AlertType.NOTIFICATION
	private _subject         : string = ''
	private _text            : string = ''

	get subject(): string {
		return this._subject;
	}
	set subject(value: string) {
		this._subject = value;
	}
	get text(): string {
		return this._text;
	}
	set text(value: string) {
		this._text = value;
	}
	get type(): AlertType {
		return this._type;
	}
	set type(value: AlertType) {
		this._type = value;
	}
}

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css'],
})
export class AlertComponent implements OnInit {

	alert           : Alert
	icon            : string
	backgroundColor : string

	constructor(alertService: AlertService) {
		this.alert = new Alert()
		this.icon = ''
		this.backgroundColor = ''
		alertService.alertToBeShown$
			.subscribe({
				next: (alert: Alert) => {
					if(!alert) return
					this.hideAlert()
					this.initAndShowAlert(alert)
				}
			})
	}

	ngOnInit() : void {
	}

	hideAlert() : void {
		this.updateAlert('0','none', '-1')
	}

	showAlert() : void {
		this.updateAlert('1','initial', '3')
	}

	/**
	 * Updates html element app-alert opacity and alert's close button touch action
	 * @param opacity
	 * @param touchAction
	 * @param zIndex
	 */
	updateAlert(opacity: string, touchAction: string, zIndex: string) : void {
		if(!opacity || !touchAction) return
		Utils.getElementAndSetVisibility('app-alert',undefined,opacity)
		const elem = document.getElementById('alert-close-button')
		elem!.style.touchAction = touchAction
		elem!.style.zIndex = zIndex
	}

	/**
	 * Inits alert object and sets the configuration for the alert type, finally shows the alert.
	 * @param newAlert
	 * @private
	 */
	private initAndShowAlert(newAlert: Alert) : void {
		if(!newAlert) return
		this.alert = newAlert
		let config = ALERT_TYPES_CONFIG[this.alert.type]
		this.backgroundColor = config.background
		this.icon = config.icon
		this.showAlert()
	}

}
