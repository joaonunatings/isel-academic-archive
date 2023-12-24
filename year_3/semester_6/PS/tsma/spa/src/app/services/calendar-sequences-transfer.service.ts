import { Injectable } from '@angular/core';
import {ShiftSequence} from "../model/dtos/calendar/calendar-create-dto";
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CalendarSequencesTransferService {

	private sequencesSource = new Subject<ShiftSequence[]>()

	sequences$ = this.sequencesSource.asObservable()

	constructor() { }

	sendSequences(s: ShiftSequence[]) {
		this.sequencesSource.next(s)
	}


}
