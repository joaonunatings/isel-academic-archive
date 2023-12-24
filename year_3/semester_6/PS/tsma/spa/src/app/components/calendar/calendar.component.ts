import {Component, HostListener, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {GridCell} from '../../model/view/grid-cell';
import {Member} from '../../model/member';
import {ShiftGridCell} from '../../model/view/shift-grid-cell';
import {Type} from '../../model/type';
import {CalendarService} from "../../services/calendar.service";
import {MemberService} from "../../services/member.service";
import {CalendarSequencesTransferService} from "../../services/calendar-sequences-transfer.service";
import {ShiftService} from "../../services/shift.service";
import {ReportComponent} from "../report/report.component";
import {TsmCalendar} from "../../model/tsm-calendar";
import {AlertService} from "../../services/alert.service";
import {MyErrorHandler} from "../../services/my-error-handler";
import {AlertType} from "../alert/alert.component";
import {MemberGridCell} from "../../model/view/member-grid-cell";

const MINIMUM_SCREEN_WIDTH     : number   = 600
const CELL_OPTIONS             : string[] = ['-','M', 'AF', 'MN', 'O', 'T', 'H', 'P', 'C', 'S', 'MS', 'HO'];
const COLOR_OPTIONS            : string[] = ['white','yellow','orange','#d3b5e5','#a8a8a8','#58f4ff',
											'lightgreen','#6c6c6c','#c948dc','#278c0d','lightcoral','#018de8'];
const INITIAL_CELL_TYPE        : string   = '-';
const INITIAL_COLOR            : string   = 'white';
const BUTTON_OPTIONS = [{backgroundColor: 'white', value: '(Backspace) Empty'},
                                {backgroundColor: 'yellow', value: '(1) Morning Shift'},
                                {backgroundColor: 'orange', value: '(2) AfterNoon Shift'},
								{backgroundColor: '#d3b5e5', value: '(3) MidNight Shift'},
								{backgroundColor: '#a8a8a8', value: '(4) Off Other Reasons'},
								{backgroundColor: '#58f4ff', value: '(5) Training'},
                                {backgroundColor: 'lightgreen', value: '(6) On Holiday'},
								{backgroundColor: '#6c6c6c', value: '(7) Patching'},
								{backgroundColor: '#c948dc', value: '(8) Coordinator'},
                                {backgroundColor: '#278c0d', value: '(9) Sick Leave'},
                                {backgroundColor: 'lightcoral', value: '(0) Missed Shift'},
								{backgroundColor: '#018de8', value: '() Home Office'}];



@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

	@ViewChild(ReportComponent)
	private reportComponent!   : ReportComponent

	public tsmCalendar!        : TsmCalendar
	public memberOptionsList   : Member[] = []

	public templateMode        : boolean  = true		// Makes distinction of which methods to execute

	public radialButtonOptions            = BUTTON_OPTIONS;
	public screenWidth         : number

	private currentTyp         : number   = 0
	private currentType        : string   = INITIAL_CELL_TYPE
	private currentColor       : string   = INITIAL_COLOR
	private changedCells       : any      = []

	constructor(private route: ActivatedRoute,
				private router: Router,
				private calendarService: CalendarService,
				private memberService: MemberService,
				private calendarSequencesService: CalendarSequencesTransferService,
				private shiftsService: ShiftService,
				private alertService: AlertService,
				private errorHandler: MyErrorHandler) {

		this.screenWidth = window.innerWidth
		this.route.params.subscribe(query => {		// check route path query string
			if (typeof(query['id']) != 'string') {		// if there is no id in route path, init template mode
				this.templateMode = true
				this.tsmCalendar = new TsmCalendar(true,calendarService,memberService,shiftsService,errorHandler)
			} else {									// if there is, init normal mode with id
				this.templateMode = false
				this.tsmCalendar = new TsmCalendar(false,calendarService,memberService,shiftsService,errorHandler,parseInt(query['id']))
			}
		})
	}

	ngOnInit(): void {
		this.resizeMatrix()
	}

	/**
	 * Updates the report component in the model after it is initialized
	 */
	ngAfterViewInit() {
		this.tsmCalendar.reportComponent = this.reportComponent
	}

	@HostListener('window:resize',['$event'])
	resizeMatrix() {
		this.screenWidth = window.innerWidth;
		let elem = document.getElementById("container-1")!
		if (this.screenWidth < MINIMUM_SCREEN_WIDTH) {
			elem.style.visibility = 'hidden'
		} else {
			elem.style.visibility = 'visible'
			elem.style.maxWidth = `${this.screenWidth - 100}px`
			elem.style.width = `${this.screenWidth - 100}px`
			elem.style.overflow = 'scroll'
		}
	}

	/**
	 * Calls api to update changed cells
	 */
	public saveChangesToCalendar() {
		if (!this.tsmCalendar.calendar) {
			this.alertService.newAlert(AlertType.SEVERE,'Calendar is not available','Can not save the changes of this calendar')
			throw new Error('saveChangesToCalendar(): Tried saving changes of undefined calendar')
		}
		// Save the shifts object from each changed cell
		let shifts : any[] = []
		for (const changedCell of this.changedCells) {
			shifts.push(changedCell.shiftCell.shift)
		}

		this.calendarService.updateShifts(this.tsmCalendar.calendar.id,shifts)
			.subscribe({
				next: () => {
					this.reportComponent.updateReportTable(this.tsmCalendar.calendar!.id, this.tsmCalendar.calendarMembersList)
					this.changedCells = []
					this.saveButtonToggle = false
					this.undoButtonToggle = false
					this.redoButtonToggle = false
					this.alertService.newAlert(AlertType.CONFIRMATION,'Changes successfully saved to calendar.','Save was successful')
				},
				error: (e) => {
					this.errorHandler.handleError(e)
				}
		})
	}

	/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *
	 *						TEMPLATE MODE USE
	 *
	 */

	/**
	 * Method called by a parent component, for template building completion, to send all the current grid shifts sequences to the
	 * calendar creation service, where these can be listened as an observable.
	 */
	public saveTemplate() : void {
		this.calendarSequencesService
			.sendSequences(this.tsmCalendar
				.parseCalendarMatrixToSequenceList())
	}


	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	*
	* 					HTML INTERFACE HELPER METHODS
	*
	*
	*/

	/**
	 * Undo/Redo/Save buttons toggle
	 */
	public undoButtonToggle : boolean = false
	public redoButtonToggle : boolean = false
	public saveButtonToggle : boolean = false

	/**
	 * Sets the current selected cell type, to be placed next time a cell is pressed.
	 * @param optIdx Option index given by the current active radio button, or hotkey pressed.
	 */
	setCurrentCellType(optIdx: number) : void {
		this.currentTyp = optIdx
		this.currentType = CELL_OPTIONS[optIdx]
		this.currentColor = COLOR_OPTIONS[optIdx]
	}

	/**
	 * Changes the cell type of the given cell to the current selected type (radio buttons).
	 * @param cell Interface cell object (ShiftGridCell).
	 */
	changeCellTypeById(cell: GridCell) : void {

		// @ts-ignore
		let currType = parseInt(Object.entries(Type)[this.currentTyp])
		let shiftCell = <ShiftGridCell>cell
		let changes : any = {}
		// If cell is the same type as the current type, no need to change
		if (shiftCell.shift.type != currType) {
			let found = this.changedCells.find( (element: any) => {	// Check if this cell was already changed once
				return element.shiftCell == shiftCell
			})
			// If not changed than register the cell as changed, else, does not alter the changed properties
			if (found == null) {
				changes.shiftCell = shiftCell
				changes.oldType = shiftCell.shift.type
				changes.oldText = shiftCell.text
				changes.oldColor = shiftCell.color
			}
			let button = document.getElementById(shiftCell.id)
			if (found == null) changes.cellButton = button
			//Change cell type information
			shiftCell.shift.type = currType
			shiftCell.text = this.currentType
			shiftCell.color = this.currentColor
			// Change cell button view properties
			button!.textContent = this.currentType
			button!.style.backgroundColor = this.currentColor
			// Undo/redo only available in normal mode
			if (!this.templateMode) {
				if (found == null) this.changedCells.push(changes)
				if (!this.saveButtonToggle) this.toggleSaveButton()
				if (!this.undoButtonToggle) this.undoButtonToggle = true
				if (this.redoButtonToggle) {
					if (found == null) this.changedCells = []
					this.redoButtonToggle = false
				}
			}
		}
	}

	private toggleSaveButton() {
		this.saveButtonToggle = !this.saveButtonToggle
	}


	@HostListener('window:keydown.Control.Z', ['$event'])
	public undoCellChanges() : void {
		if (this.undoButtonToggle) {
			for (const obj of this.changedCells) {
				obj.doText = obj.cellButton.textContent
				obj.doColor = obj.cellButton.style.backgroundColor
				obj.doType = obj.shiftCell.shift.type

				obj.cellButton.textContent = obj.oldText
				obj.cellButton.style.backgroundColor = obj.oldColor

				obj.shiftCell.shift.type = obj.oldType
				obj.shiftCell.text = obj.oldText
				obj.shiftCell.color = obj.oldColor
			}
			this.saveButtonToggle = false
			this.undoButtonToggle = false
			this.redoButtonToggle = true
		}
	}

	@HostListener('window:keydown.Control.Shift.z', ['$event'])
	public redoCellChanges() : void {
		if (this.redoButtonToggle) {
			for (const obj of this.changedCells) {
				obj.cellButton.textContent = obj.doText
				obj.cellButton.style.backgroundColor = obj.doColor

				obj.shiftCell.shift.type = obj.doType
				obj.shiftCell.text = obj.doText
				obj.shiftCell.color = obj.doColor
			}
			this.saveButtonToggle = true
			this.undoButtonToggle = true
			this.redoButtonToggle = false
		}
	}


	// Current paint mode.
	private paint : boolean = false

	/**
	 * Sets the paint mode to the one given.
	 * @param p boolean
	 */
	public setPaintMode(p: boolean) : void {
		this.paint = p
	}

	/**
	 * If paint mode is True, then paint the given cell.
	 * @param cell
	 */
	public toPaint(cell: GridCell) : void{
		if (this.paint) {
			this.changeCellTypeById(cell)
		}
	}

	/**
	 *  Function to be called by interface to add a new row.
	 */
	public addRow() : void {
		this.tsmCalendar.addRow()
	}

	/**
	 *  Function to be called by interface to remove a row.
	 */
	public removeRow() : void {
		this.tsmCalendar.deleteRow()
	}

	/**
	 *  Function to be called by interface, to add a new column.
	 */
	public addColumn() : void {
		this.tsmCalendar.addColumn()
	}

	/**
	 *  Function to be called by interface, to remove a column.
	 */
	public removeColumn() : void {
		this.tsmCalendar.deleteColumn()
	}


	@HostListener('window:keydown', ['$event'])
	handleKeyDown(event: KeyboardEvent) {
		switch(event.key) {
			case 'Backspace':
				this.setCurrentCellType(0);
				break;
			case '1':
				this.setCurrentCellType(1);
				break;
			case '2':
				this.setCurrentCellType(2);
				break;
			case '3':
				this.setCurrentCellType(3);
				break;
			case '4':
				this.setCurrentCellType(4);
				break;
			case '5':
				this.setCurrentCellType(5);
				break;
			case '6':
				this.setCurrentCellType(6);
				break;
			case '7':
				this.setCurrentCellType(7);
				break;
			case '8':
				this.setCurrentCellType(8);
				break;
			case '9':
				this.setCurrentCellType(9);
				break;
			case '0':
				this.setCurrentCellType(10);
				break;
		}
	}

	/* --- Member addition functions --- */

	private selectedMembers : Member[] = []
	/**
	 * Method to be called when a member box is pressed in shown dropdown.
	 * Updates view member cell with the information of the new member
	 * @param m		Member object
	 * @param mCell	View cell object
	 */
	public selectMemberFromOptions(m: Member, mCell: GridCell) {
		const optionIdx = this.memberOptionsList.indexOf(m)
		this.memberOptionsList.splice(optionIdx,1)
		this.selectedMembers.push(m)
		let cell = <MemberGridCell>mCell

		const idx = this.selectedMembers.indexOf(cell.member)
		if (idx != -1) {
			this.selectedMembers.splice(idx,1)
			this.memberOptionsList.unshift(cell.member)
		}

		cell.text = m.name
		cell.member = m
	}

	private lock : boolean = false
	/**
	 * Shows a dropdown menu with all members
	 * @param memberCell
	 */
	public showDropdown(memberCell: GridCell) {
		if (this.memberOptionsList.length < 1) this.memberOptionsList = this.tsmCalendar.calendarMembersList
		if (!this.lock && this.templateMode) {
			//console.log('SHOW DROPDOWN')
			let elem = document.getElementById('m'+memberCell.id)
			elem!.style.visibility = "visible"
			elem!.style.opacity = "1"
			this.lock = true
		}
	}

	/**
	 * Hides a shown dropdown
	 * @param memberCellID
	 */
	public hideDropdown(memberCellID : string) {
		//console.log('HIDE DROPDOWN')
		let elem = document.getElementById("m"+memberCellID)
		elem!.style.visibility = "hidden"
		elem!.style.opacity = "0"
		this.lock = false
	}

	//TODO: make only one selection at a time, save row, and check it next call
	private select : boolean = true
	public selectRow(row: GridCell[]) : void {
		let idx = 0
		let elem = null;
		let elemID = "";
		for (let cell of row) {
			elemID = idx === 0 ? "member-" + cell.id : cell.id;
			elem = document.getElementById(elemID)
			if (elem != null) {
				let color, op;
				if (this.select) {
					//color = "cornflowerblue"
					color = "#ffffff"
					op = "90%"
				} else {
					//color = cell.color
					color = "black"
					op = "1"
				}
				//elem.style.backgroundColor = color
				elem.style.opacity = op
				elem.style.borderColor = color
			}
			idx = 1;
		}
		this.select = !this.select
	}
}
