import {Component, OnInit} from '@angular/core';
import {AlertService} from "../../services/alert.service";
import {Alert, AlertType} from "../alert/alert.component";

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {

	constructor() {
	}

	ngOnInit(): void {
	}

}
