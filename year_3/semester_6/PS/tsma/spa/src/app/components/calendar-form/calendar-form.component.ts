import {Component, HostListener, OnInit, ViewChild} from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import {CalendarCreateDTO, ShiftSequence} from "../../model/dtos/calendar/calendar-create-dto";
import {CalendarSequencesTransferService} from "../../services/calendar-sequences-transfer.service";
import {CalendarService} from "../../services/calendar.service";
import {CalendarComponent} from "../calendar/calendar.component";
import {Router} from '@angular/router';
import {MyErrorHandler} from "../../services/my-error-handler";

const DEFAULT_INITIAL_WIDTH = 1000

@Component({
  selector: 'app-calendar-form',
  templateUrl: './calendar-form.component.html',
  styleUrls: ['./calendar-form.component.css'],
  providers: [CalendarSequencesTransferService]
})
export class CalendarFormComponent implements OnInit {

	@ViewChild(CalendarComponent)
	private child?            : CalendarComponent

	private calendarDTO?      : CalendarCreateDTO
	private calendarSequences : ShiftSequence[]  = []

	public windowWidth        : string = `${DEFAULT_INITIAL_WIDTH}px`
	public showTooltip        : boolean = false

	public calendarForm = new UntypedFormGroup({
		calendarTitle: new UntypedFormControl('',Validators.required),
		calendarDescription: new UntypedFormControl(''),
		calendarStartDate: new UntypedFormControl('',Validators.required),
		calendarEndDate: new UntypedFormControl('',Validators.required),
	})

	constructor(private calendarCreatorService: CalendarSequencesTransferService,
				private calendarService: CalendarService,
				private router: Router,
				private errorHandler: MyErrorHandler
	) {
		calendarCreatorService.sequences$.subscribe({
			next: (sequences) => {
				if (sequences) this.calendarSequences = sequences
			},
			error: err => {
				this.errorHandler.handleError(err)
			}
		})
	}

	ngOnInit(): void {
		this.resizeForm()
	}

	public createCalendarFromTemplate() : void {
		if(!this.child){
			this.errorHandler.showErrorAlert('Something went wrong, failed to process template, please try again later.')
			throw new Error('Error: CalendarFormComponent: createCalendarFromTemplate(): Child CalendarComponent is undefined.')
		}
		this.child.saveTemplate()
		let formValue = this.calendarForm.value
		this.calendarDTO = new CalendarCreateDTO(formValue.calendarTitle, formValue.calendarDescription,
												formValue.calendarStartDate, formValue.calendarEndDate,
												this.calendarSequences)
		// send object to API
		this.calendarService.createCalendar(this.calendarDTO)
			.subscribe({
				next: (data) => {
					if (data) {
						this.router.navigate(['/calendars'])
					}
				},
				error: err => {
					this.errorHandler.handleError(err)
				}
		})
	}

	public toggleTooltip() {
		this.showTooltip = !this.showTooltip
	}

	@HostListener('window:resize',['$event'])
	private resizeForm() {
		this.windowWidth = `${window.innerWidth}px`
	}
}
