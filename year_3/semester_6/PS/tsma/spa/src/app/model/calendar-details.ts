export class CalendarDetails {

	readonly id : number
	title       : string
	description : string

	startDate   : Date
	endDate     : Date

	constructor(id:number,
				title:string,
				description:string,
				startDate:Date,
				endDate:Date) {
		this.id = id
		this.title = title
		this.description = description
		this.startDate = startDate
		this.endDate = endDate
	}
}
