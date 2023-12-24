import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {CalendarDetails} from "../model/calendar-details";
import {CalendarDTO} from "../model/dtos/calendar/calendar-dto";
import {CalendarCreateDTO} from "../model/dtos/calendar/calendar-create-dto";
import {CalendarInfoDTO} from "../model/dtos/calendar/calendar-info-dto";
import {environment} from "../../environments/environment";
import {Utils} from "../utils/utils";
import {Shift} from "../model/shift";


@Injectable({
  providedIn: 'root'
})
export class CalendarService {

	private readonly calendarsUrl : string

	constructor(private http: HttpClient) {
		this.calendarsUrl = environment.apiUrl + 'calendars'
	}

	private getCalendars(ids?:number[], titles?: string[], sDate?: string, eDate?: string, sortBy?:string[], pageSize?:number, pageNumber?:number) : Observable<CalendarDetails[]> {
		let url: URL = Utils.getURLWithQueryParametersOf(this.calendarsUrl,ids,sortBy,pageSize,pageNumber)
		if (titles && titles.length > 0) url.searchParams.append("titles",titles.toString())
		if (sDate) url.searchParams.append("startDate",sDate)
		if (eDate) url.searchParams.append("endDate",eDate)
		return this.http.get<CalendarDetails[]>(url.toString())
	}

	public getAllCalendars() : Observable<CalendarDetails[]>{
		return this.getCalendars()
	}

	public getCalendar(id: number) : Observable<CalendarDTO> {
		return this.http.get<CalendarDTO>(this.calendarsUrl+`/${id}`)
	}

	public createCalendar(calendar: CalendarCreateDTO) : Observable<CalendarInfoDTO> {
		return this.http.post<CalendarInfoDTO>(this.calendarsUrl, calendar)
	}

	public deleteCalendar(id : number) : Observable<number>{
		return this.http.delete<number>(this.calendarsUrl+`/${id}`)
	}

	public updateShifts(calendarId: number, shifts: Shift[]) : Observable<Shift[]> {
		return this.http.patch<Shift[]>(this.calendarsUrl+`/${calendarId}/shifts`,shifts)
	}
}
