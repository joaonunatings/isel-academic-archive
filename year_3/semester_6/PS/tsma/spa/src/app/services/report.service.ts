import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {ReportDTO} from "../model/dtos/report/report-dto";
import {Utils} from "../utils/utils";

@Injectable({
  providedIn: 'root'
})
export class ReportService {

	private readonly reportsUrl : string

	constructor(private http : HttpClient) {
		this.reportsUrl = environment.apiUrl + 'reports'
	}

	public getCalendarReport(calendarID: number, sDate?: string, eDate?: string, sortBy?:string[], pageNo?: number, pageSize?: number) : Observable<any> {
		let url = Utils.getURLWithQueryParametersOf(this.reportsUrl,undefined,sortBy,pageSize,pageNo)
		url.searchParams.append("calendars",calendarID.toString())
		if (sDate) url.searchParams.append("startDate",sDate)
		if (eDate) url.searchParams.append("endDate",eDate)
		return this.http.get<ReportDTO[]>(url.toString()) //?sort=member,asc&sort=shiftType,asc`
	}
}
