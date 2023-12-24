import {ShiftSequence} from "./calendar-create-dto";

export class CalendarDTO {

	readonly id  : number
	title       : string
	description : string

	startDate   : Date
	endDate     : Date

	sequences : ShiftSequence[]

	constructor(id:number,
				title:string,
				description:string,
				startDate:Date,
				endDate:Date,
				sequences:ShiftSequence[]) {
		this.id = id
		this.title = title
		this.description = description
		this.startDate = startDate
		this.endDate = endDate
		this.sequences = sequences
	}
}
