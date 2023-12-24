import {ErrorHandler, Injectable} from "@angular/core";
import {Router} from "@angular/router";
import {AlertService} from "./alert.service";
import {Alert, AlertType} from "../components/alert/alert.component";
import {HttpErrorResponse} from "@angular/common/http";
import {ErrorConverter} from "../utils/error-converter";

@Injectable()
export class MyErrorHandler implements ErrorHandler{

	constructor(private alertService?: AlertService, private router?: Router) {
	}

	handleError(error: any): void {
		if (!error) return
		let convertedError = ErrorConverter.convertError(error.error)
		this.buildErrorAndSendAlert(convertedError.body,convertedError.status,undefined)
		throw error
	}

	private static showWindowAlert(statusCode: number, message: string) : void {
		window.alert(`Error:\n\tCode: ${statusCode}\n\tMessage: ${message}`)
	}

	private goToErrorPage(statusCode: number, message: string) : void {
		this.router!.navigate(['error'], {queryParams: {statusCode: statusCode, message: message}})
	}

	private sendErrorToAlert(error: any) : void {
		const subject = `ERROR`
		if(error instanceof HttpErrorResponse) subject.concat(` ${error.status} - ${error.statusText}`)
		const message = error.message
		this.alertService!.showAlert(MyErrorHandler.createAndPopulateAlert(AlertType.SEVERE, message, subject))
	}

	private buildErrorAndSendAlert(message: string, statusCode?: number, subject?: string) : void {
		let header = 'ERROR'
		if (statusCode) header = `${statusCode} ` + header
		if (subject) header = header + ` - ${subject}`
		this.alertService!.showAlert(MyErrorHandler.createAndPopulateAlert(AlertType.SEVERE, message, header))
	}

	public showErrorAlert(message: string, statusCode?: number, subject?: string) : void {
		let status: number | undefined
		if (statusCode) status = statusCode
		this.buildErrorAndSendAlert(message, status, subject)
	}

	private static createAndPopulateAlert(type: AlertType, text: string, subject: string) : Alert {
		const alert = new Alert()
		alert.subject = subject
		alert.text = text
		alert.type = type
		return alert
	}
}
