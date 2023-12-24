import {Component, OnInit} from '@angular/core';
import {CalendarDetails} from "../../model/calendar-details";
import {CalendarService} from "../../services/calendar.service";
import {MyErrorHandler} from "../../services/my-error-handler";
import {GraphSyncService} from "../../services/graph-sync.service";
import {AlertService} from "../../services/alert.service";
import {AlertType} from "../alert/alert.component";

@Component({
	selector: 'app-home-page',
	templateUrl: './home-page.component.html',
	styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

	public calendars      : CalendarDetails[] = []
	public syncButtonLock : boolean = false

	constructor(private calendarService: CalendarService,
				private graphSyncService: GraphSyncService,
				private alertService: AlertService,
				private errorHandler: MyErrorHandler) {
		// Get all calendars from api
		this.calendarService.getAllCalendars()
			.subscribe({
				next: (calendars) => {
					if (calendars && calendars.length > 0){
						this.calendars = calendars
						this.calendars.forEach(elem => {
							elem.startDate = new Date(elem.startDate.toString().replace(/-/g,"/"))
							elem.endDate = new Date(elem.endDate.toString().replace(/-/g,"/"))
						})
					}
				},
				error: (error) => {
					errorHandler.handleError(error)
				}
		})
	}

	ngOnInit(): void { }

	public deleteCalendarById(id: number) {
		this.calendarService.deleteCalendar(id).subscribe({
			next: (id) => {
				if (id != null) {
					let cal = this.calendars.find(element => element.id == id)!
					this.calendars.splice(this.calendars.indexOf(cal), 1)
				}
			},
			error: (error) => {
				this.errorHandler.handleError(error)
			}
		})
	}

	public showPopupById(id: number) {
		let elem = document.getElementById(`popup-${id}`)
		if (elem != null) {
			elem.style.visibility = "visible"
			elem.style.opacity = "1"
		}
	}

	public hidePopupById(id: number) {
		let elem = document.getElementById(`popup-${id}`)
		if (elem != null) {
			elem.style.visibility = "hidden"
			elem.style.opacity = "0"
		}
	}

	public syncCalendarsWithGraph() : void {
		this.syncButtonLock = true
		this.graphSyncService.syncWithGraph()
			.subscribe({
				next: () => {
					this.syncButtonLock = false
					this.alertService.newAlert(AlertType.CONFIRMATION,'Finished Syncing','Syncing Complete')
				},
				error: (err) => {
					this.syncButtonLock = false
					this.errorHandler.handleError(err)
				}
			})
	}

}

