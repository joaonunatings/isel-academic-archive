import {Type} from "../../type";

export class CalendarCreateDTO {
	title! 	     : string
	description  : string = ""

	startDate!   : string
	endDate!     : string

	sequences!	 : ShiftSequence[]

	constructor(title: string, description: string, startDate: string, endDate: string, sequences: ShiftSequence[]) {
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sequences = sequences;
	}
}

export class ShiftSequence {

	 memberId : number
	 sequence : Type[]

	constructor(memberId: number, sequence: Type[]) {
		this.memberId = memberId;
		this.sequence! = sequence;
	}

}
