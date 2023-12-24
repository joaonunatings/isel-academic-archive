import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {MyErrorHandler} from "../../services/my-error-handler";

@Component({
  selector: 'app-error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.css']
})
export class ErrorPageComponent implements OnInit {

	constructor(private route: ActivatedRoute, private errorHandler: MyErrorHandler) {
		this.route.queryParams.subscribe(data => {
			if (data['statusCode'] != null) {
				errorHandler.showErrorAlert(data['message'],data['statusCode'],'Woopsy Daisy')
			} else {
				errorHandler.showErrorAlert('You are not supposed to be here, try again!',404,'Woopsy Daisy')
			}
		})
	}

	ngOnInit(): void {
	}

}
