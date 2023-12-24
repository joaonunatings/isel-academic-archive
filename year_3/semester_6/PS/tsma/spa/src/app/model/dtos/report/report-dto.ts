import {Type} from "../../type";

export class ReportDTO {

	totalShifts : number
	memberId   : number
	shiftType  : Type

	constructor(totalShifts: number, memberId: number, shiftType: Type) {
		this.totalShifts = totalShifts;
		this.memberId = memberId;
		this.shiftType = shiftType;
	}
}
