import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {Shift} from "../model/shift";
import {Type} from "../model/type";
import {Utils} from "../utils/utils";

@Injectable({
  providedIn: 'root'
})
export class ShiftService {

	private readonly shiftsUrl: string

  	constructor(private http: HttpClient) {
		this.shiftsUrl = environment.apiUrl + 'shifts'
	}

	public getShifts(calendarIds?: number[], memberIds?: number[], sDate?: string, eDate?: string, types?: Type[],
					  pageNo?: number, pageSize?: number, sortBy?: string[]) : Observable<Shift[]> {
		let url = Utils.getURLWithQueryParametersOf(this.shiftsUrl,undefined,sortBy,pageSize,pageNo)
		if (calendarIds && calendarIds.length > 0) url.searchParams.append("calendars", calendarIds.toString())
		if (memberIds && memberIds.length > 0) url.searchParams.append("members", memberIds.toString())
		if (sDate) url.searchParams.append("startDate", sDate)
		if (eDate) url.searchParams.append("endDate", eDate)
		if (types && types.length > 0) url.searchParams.append("types", types.toString())
		return this.http.get<Shift[]>(url.toString())
	}

}
