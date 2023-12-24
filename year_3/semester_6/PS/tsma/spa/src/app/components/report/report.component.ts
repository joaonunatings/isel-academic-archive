import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ReportService} from "../../services/report.service";
import {Member} from "../../model/member";
import {ReportDTO} from "../../model/dtos/report/report-dto";
import {MyErrorHandler} from "../../services/my-error-handler";
import {FormControl, FormGroup} from "@angular/forms";

const TYPES_AMOUNT : number = 12
const REPORT_HEADERS = [{backgroundColor: 'white', value: '-'},
	{backgroundColor: 'yellow', value: 'M'},
	{backgroundColor: 'orange', value: 'AF'},
	{backgroundColor: '#d3b5e5', value: 'MN'},
	{backgroundColor: '#a8a8a8', value: 'O'},
	{backgroundColor: '#58f4ff', value: 'T'},
	{backgroundColor: 'lightgreen', value: 'H'},
	{backgroundColor: '#6c6c6c', value: 'P'},
	{backgroundColor: '#c948dc', value: 'C'},
	{backgroundColor: '#278c0d', value: 'S'},
	{backgroundColor: 'lightcoral', value: 'MS'},
	{backgroundColor: '#018de8', value: 'HO'}]

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {

	private calendarId : number = -1;
	private calendarMembers : Member[] = [];

	public reportHeaders = REPORT_HEADERS
	public reportMatrix  : string[][] = []

	constructor(private reportService : ReportService, private errorHandler: MyErrorHandler) { }

	ngOnInit(): void { }

	public updateReportTable(calendarId: number, calendarMembers: Member[], sDate?: string, eDate?: string) : void {
		this.calendarId = calendarId
		this.calendarMembers = calendarMembers
		this.reportService.getCalendarReport(this.calendarId, sDate, eDate,
										["member,asc","shiftType,asc"],undefined,
										this.calendarMembers.length*12)
			.subscribe({
				next: res => {
					if (res) {
						this.initReportTable(res)
					}
				},
				error: (err) => {
					this.initEmptyReportTable()
					this.errorHandler.handleError(err)
				}
		})
	}

	private initReportTable(reports: ReportDTO[]) : void {
		this.reportMatrix = []
		let aux : string[] = []
		let types = TYPES_AMOUNT
		let aux2 = 0
		for (let m of this.calendarMembers) {
			aux.push(m.name)
			for (let j = aux2; j < aux2+types; j++) {
				aux.push(String(reports[j].totalShifts))
			}
			this.reportMatrix.push(aux)
			aux = []
			aux2 += types
		}
	}

	private initEmptyReportTable() : void {
		this.reportMatrix = []
		let aux : string[] = []
		for (let m of this.calendarMembers) {
			aux.push(m.name)
			for (let j = 0; j < this.reportHeaders.length; j++) {
				aux.push("0")
			}
			this.reportMatrix.push(aux)
			aux = []
		}
	}



}
