import {CalendarDTO} from "./dtos/calendar/calendar-dto";
import {GridCell} from "./view/grid-cell";
import {CalendarService} from "../services/calendar.service";
import {Member} from "./member";
import {MemberGridCell} from "./view/member-grid-cell";
import {ShiftGridCell} from "./view/shift-grid-cell";
import {Shift} from "./shift";
import {Type} from "./type";
import {MemberService} from "../services/member.service";
import {Observable} from "rxjs";
import {ReportComponent} from "../components/report/report.component";
import {ShiftSequence} from "./dtos/calendar/calendar-create-dto";
import {ShiftService} from "../services/shift.service";
import {MyErrorHandler} from "../services/my-error-handler";

const DEFAULT_COLUMN_NUMBER           : number   = 10;
const DEFAULT_ROW_NUMBER              : number   = 3;

const MATRIX_WEEK_HEADERS_TEMPLATE    : string[] = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
const CELL_TEXT_OPTIONS               : string[] = ['-','M', 'AF', 'MN', 'O', 'T', 'H', 'P', 'C', 'S', 'MS', 'HO'];
const CELL_COLOR_OPTIONS              : string[] = ['white','yellow','orange','#d3b5e5','#a8a8a8','#58f4ff',
	'lightgreen','#6c6c6c','#c948dc','#278c0d','lightcoral','#018de8'];

const INITIAL_CELL_TYPE_TEXT          : string   = '-';
const INITIAL_COLOR                   : string   = 'white';

export class TsmCalendar {
	private _reportComponent      : ReportComponent | undefined
	/**
	 * Calendar data transfer object sent by the tsma api
	 */
	private _calendar             : CalendarDTO
	private _calendarShifts 	  : Shift[] | undefined
	/**
	 * Flag that indicates if this calendar is working in template mode
	 */
	private templateMode          : boolean = true

	private _calendarDateHeaders  : string[] = []
	private _calendarWeekHeaders  : string[] = [];
	private _calendarMatrix       : GridCell[][] = [];
	private _calendarMembersList  : Member[] = []
	private calendarMembersIds    : number[] = []

	public currentColNumber       : number = DEFAULT_COLUMN_NUMBER;
	public currentRowCount        : number = DEFAULT_ROW_NUMBER;

	constructor(templateMode: boolean,
				private calendarService: CalendarService,
				private memberService: MemberService,
				private shiftService: ShiftService,
				private errorHandler: MyErrorHandler,
				calendarId?: number,
				calendarReportComponent?: ReportComponent) {
		this.templateMode = templateMode;
		this._calendar = new CalendarDTO(-1,"","",new Date(""),new Date(""),[])
		this._reportComponent = calendarReportComponent
		this.initCalendar(templateMode, calendarId)
	}

	/**
	 * Initializes the calendar model object, depending on the provided mode
	 * @param templateMode Provided mode, true if template mode, false if normal mode
	 * @param calendarId Provided calendar id for normal mode use. Used when requesting the calendar with id from api
	 */
	private initCalendar(templateMode: boolean, calendarId?: number) : void {
		if (templateMode) {
			// Initialize calendar matrix and headers for template use
			this.initEmptyMatrix();
			this.initEmptyMatrixHeaders()
			this.memberService.getAllMembers().subscribe({
				next: members => {
					if (members != null)
						this._calendarMembersList = members
				},
				error: (e) => {
					this.errorHandler.handleError(e)
				}
			})
		} else {
			// Initialize calendar matrix and headers for normal use
			if (!calendarId) {
				this.errorHandler.showErrorAlert('Could not initialize the calendar, sorry for the inconvenience.',
														404,'Failed to open calendar')
				throw new Error('Missing calendar id in initialization. Can not initialize calendar.')
			}
			this.calendarService.getCalendar(calendarId)
				.subscribe({
					next: (calendar) => {
						if (calendar){
							this._calendar = calendar
							this.extractCalendarInfo(calendar)
						}
					},
					error: (e) => {
						this.errorHandler.handleError(e)
					}
				})
		}
	}

	/* - - - - - - - - - - - - - Template Mode- - - - - - - - - - - - - - - */

	/**
	 * Adds a new empty row to the matrix, with the same column number as the current matrix.
	 */
	public addRow() : void {
		this._calendarMatrix.push(TsmCalendar.fillEmptyRow(this._calendarMatrix.length, this.currentColNumber))
		this.currentRowCount++
	}

	/**
	 * Deletes the last row of the matrix
	 */
	public deleteRow() : void {
		if (this._calendarMatrix.length > 0) {
			this._calendarMatrix.splice(this._calendarMatrix.length-1,1)
			this.currentRowCount--
		}
	}

	/**
	 * Adds a new column to the matrix, adds a new element to the headers and a new empty shiftCell for each row.
	 */
	public addColumn() : void {
		this._calendarDateHeaders.push(' ')
		this._calendarWeekHeaders.push(' ')
		this.currentColNumber++
		let index = 0
		this._calendarMatrix.forEach(row => {
			row.push(new ShiftGridCell(`${index++}-${this.currentColNumber}`,
						INITIAL_CELL_TYPE_TEXT,
						INITIAL_COLOR,
						new Shift(0,new Date(),Type.REST))
			)
		})
	}

	/**
	 * Deletes the last column of the matrix, deletes one cell from each header and matrix rows
	 */
	public deleteColumn() : void {
		if (this.currentColNumber > 0) {
			this._calendarDateHeaders.splice(this._calendarDateHeaders.length-1,1)
			this._calendarWeekHeaders.splice(this._calendarWeekHeaders.length-1,1)
			this._calendarMatrix.forEach(row => {
				row.splice(row.length-1,1)
			})
			this.currentColNumber--
		}
	}


	/**
	 * Initializes the matrix with the default number of rows and columns.
	 * Each start of row, is initialized with a Member cell, and the rest is filled with shift cells.
	 */
	private initEmptyMatrix() : void {
		for (let index = 0; index < DEFAULT_ROW_NUMBER; index++) {
			this._calendarMatrix.push(TsmCalendar.fillEmptyRow(index, DEFAULT_COLUMN_NUMBER));
		}
	}

	/**
	 *  Fills an array with one member cell and the remaining with shift cells.
	 * @param idx      The row index of the matrix.
	 * @param colCount The total number of columns to be added in the row.
	 * @returns        The filled array, representing a matrix row and a member calendar.
	 */
	private static fillEmptyRow(idx: number, colCount: number) : GridCell[] {
		let member = new Member(idx,'Choose a member','') // init empty member
		let cellArray: GridCell[] = [new MemberGridCell(`${member.id}`,member.name, INITIAL_COLOR, member)];
		for (let i = 0; i < colCount; i++) {
			cellArray.push(new ShiftGridCell(`${idx}-${i}`, '-', INITIAL_COLOR, new Shift(0,new Date(),Type.REST)))
		}
		return cellArray;
	}

	/**
	 * Initialize matrix headers with empty cells
	 */
	private initEmptyMatrixHeaders() {
		for (let i = 0; i < DEFAULT_COLUMN_NUMBER; i++) {
			this._calendarDateHeaders.push(' ')
			this._calendarWeekHeaders.push(' ')
		}
	}

	/**
	 * Returns the calendar matrix in sequenceDTO list form.
	 */
	public parseCalendarMatrixToSequenceList() : ShiftSequence[] {
		let sequencesList: ShiftSequence[] = []
		let ignoreFirst = true
		for (const row of this._calendarMatrix) {
			const memberCell: MemberGridCell = <MemberGridCell> row[0]
			if (memberCell.member.name == 'Choose a member') {
				this.errorHandler.showErrorAlert('Complete the calendar template with the missing member, or remove it',
					undefined,
					'Can not create a calendar with undefined member')
				throw new Error('Tried to create calendar with undefined member')
			}
			const memberSequences: Type[] = []
			ignoreFirst = true

			for (const cell of row) {
				if (ignoreFirst) {
					ignoreFirst = false
					continue
				}
				memberSequences.push((<ShiftGridCell>cell).shift.type)
			}
			sequencesList.push(new ShiftSequence(memberCell.member.id, memberSequences))
		}
		return sequencesList
	}


	/* - - - - - - - - - - - - - Normal Mode- - - - - - - - - - - - - - - */

	/**
	 * Extracts information from the calendarDTO, the column and row number of the calendar, the members of the calendar
	 * and loads the shift's information to the matrix model
	 */
	private extractCalendarInfo(calendar: CalendarDTO) : void {
		this.currentColNumber = TsmCalendar.calculateCalendarLength(calendar)
		this.currentRowCount = calendar.sequences.length
		// Get calendar shifts
		this.shiftService.getShifts([calendar.id],undefined,undefined,undefined,
			undefined, undefined, this.currentColNumber*this.currentRowCount+1,['member,asc','date,asc'])
				.subscribe({
					next: (shifts) => {
						if (shifts && shifts.length > 0) {
							this._calendarShifts = shifts
							this.getCalendarMembers(calendar)
								.subscribe({
									next: (members) => {
										if (members && members.length > 0) {
											this._calendarMembersList = members
											this.loadCalendarToMatrix(shifts, this.currentColNumber, this.currentRowCount)
											this._reportComponent?.updateReportTable(calendar.id,this._calendarMembersList)		//TODO: Revise this
										}
									},
									error: (e) => {
										this.errorHandler.handleError(e)
									}
								})
						}
					},
					error: (e) => {
						this.errorHandler.handleError(e)
					}
				})

	}


	/**
	 * Calculates the number of days between start and end dates of provided calendar.
	 * @param calendar A calendar data transfer object from tsma API
	 */
	private static calculateCalendarLength(calendar: CalendarDTO) : number{
		let diff = new Date(calendar.endDate).getTime() - new Date(calendar.startDate).getTime()
		return diff / (1000 * 3600 * 24)
	}

	/**
	 * Extracts all calendar members ids from the calendar DTO, and requests those members using the memberService
	 * CalendarDTO obtained from the api contains the shift sequences for each member, ordered by member id.
	 * For each shift sequence, save its member id to be used in the following getMembers() call.
	 * @param calendar The calendar DTO
	 */
	private getCalendarMembers(calendar: CalendarDTO) : Observable<Member[]>{
		for (const sequence of calendar.sequences) {
			this.calendarMembersIds.push(sequence.memberId)
		}
		return this.memberService.getMembers(this.calendarMembersIds)
	}

	/**
	 * Computes the information in CalendarDTO and its members list to the interface matrix
	 * @param shifts			Calendar shifts list retrieved from API
	 * @param calendarLength	Calendar length in days
	 * @param memberCount		Number of members in the calendar
	 */
	private loadCalendarToMatrix(shifts: Shift[], calendarLength: number, memberCount: number) : void {
		let matrix: GridCell[][] = []
		for (let idx = 0; idx < memberCount; idx++) {
			let member = this._calendarMembersList[idx]
			let newRow: GridCell[] = [new MemberGridCell(`${member.id}`,member.name, INITIAL_COLOR, member)];
			// For each batch of member shifts (calendar.shifts is continuous array)
			for (let i = idx*calendarLength; i < (idx+1)*calendarLength; i++) {
				let shift = shifts[i]
				// for the first N (N = calendarLength) shifts add the dates to the calendar headers
				if (idx==0) {
					this.addDateToHeaders(shift.date)
				}
				// shift id = calendarID+MemberID+DATE (database id),  shiftViewCell id = Y-X
				// @ts-ignore
				newRow.push(new ShiftGridCell(`${idx}-${i+1}`, CELL_TEXT_OPTIONS[Type[shift.type]], CELL_COLOR_OPTIONS[Type[shift.type]], new Shift(shift.memberId,shift.date,shift.type)))
			}
			matrix.push(newRow)
		}
		this._calendarMatrix = matrix
	}

	/**
	 * Adds date and week day to the matrix headers
	 * @param date Given date, with string format
	 */
	private addDateToHeaders(date: Date) {
		let d = new Date(date)
		this.calendarDateHeaders.push(`${d.getDate()}/${d.getMonth()+1}`)
		this.calendarWeekHeaders.push(MATRIX_WEEK_HEADERS_TEMPLATE[d.getDay()])
	}

	/* - - - - - - - - - - - - Getters/Setters - - - - - - - - - - - - - */


	get calendarDateHeaders(): string[] {
		return this._calendarDateHeaders;
	}

	get calendarWeekHeaders(): string[] {
		return this._calendarWeekHeaders;
	}

	get calendarMatrix(): GridCell[][] {
		return this._calendarMatrix;
	}

	get calendar(): CalendarDTO | undefined {
		return this._calendar;
	}

	get calendarMembersList(): Member[] {
		return this._calendarMembersList;
	}

	set reportComponent(value: ReportComponent | undefined) {
		this._reportComponent = value;
	}
}
