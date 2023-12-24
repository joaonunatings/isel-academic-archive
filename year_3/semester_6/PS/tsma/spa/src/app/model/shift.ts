import { Type } from "./type";

export class Shift {

	memberId : number
	date     : Date
    type     : Type

	constructor(memberId: number, data: Date, type: Type) {
		this.memberId = memberId;
		this.date = data;
		this.type = type;
	}
}
