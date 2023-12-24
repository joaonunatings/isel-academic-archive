import { TestBed } from '@angular/core/testing';

import { CalendarService } from './calendar.service';
import {HttpClient} from "@angular/common/http";
import {of} from "rxjs";
import {CalendarInfoDTO} from "../model/dtos/calendar/calendar-info-dto";
import {CalendarDTO} from "../model/dtos/calendar/calendar-dto";
import {CalendarCreateDTO} from "../model/dtos/calendar/calendar-create-dto";

describe('CalendarService', () => {
	let calendarService: CalendarService;
	let httpClientSpy: jasmine.SpyObj<HttpClient>;

	beforeEach(() => {
		const spy = jasmine.createSpyObj('HttpClient', ['get','post','delete'])

		TestBed.configureTestingModule({
			providers: [
				CalendarService,
				{ provide: HttpClient, useValue: spy }
			]
		});
		calendarService = TestBed.inject(CalendarService);
		httpClientSpy = TestBed.inject(HttpClient) as jasmine.SpyObj<HttpClient>
	});

	it('should be created', () => {
		expect(calendarService).toBeTruthy();
	});

	it('should get expected calendar by id', function (done: DoneFn) {
		const expectedCalendar = new CalendarDTO(1,'title','desc',
													new Date(), new Date(), [])

		httpClientSpy.get.and.returnValue(of(expectedCalendar))

		calendarService.getCalendar(expectedCalendar.id)
			.subscribe({
				next: (calendar) => {
					expect(calendar)
						.withContext('expected calendar')
						.toEqual(expectedCalendar)
					done()
				},
				error: () => {
					done.fail('failed to get expected calendar')
				}
			})
		expect(httpClientSpy.get.calls.count())
			.withContext('one call')
			.toBe(1)
	});

	it('should create expected calendar', function (done: DoneFn) {
		const expectedCalendarInfo = new CalendarInfoDTO(1,'calendar title','calendar description')
		const calendarToCreate = new CalendarCreateDTO('calendar title','calendar description',
												'','',[])

		httpClientSpy.post.and.returnValue(of(expectedCalendarInfo))

		calendarService.createCalendar(calendarToCreate)
			.subscribe({
				next: (calendarInfo) => {
					expect(calendarInfo)
						.withContext('created calendar info')
						.toEqual(expectedCalendarInfo)
					done()
				},
				error: () => {
					done.fail('Failed to create calendar')
				}
			})
		expect(httpClientSpy.post.calls.count())
			.withContext('one call')
			.toBe(1)
	});

	it('should delete calendar by id', function (done: DoneFn) {
		const expectedId = 1

		httpClientSpy.delete.and.returnValue(of(expectedId))

		calendarService.deleteCalendar(expectedId)
			.subscribe({
				next: (deletedId) => {
					expect(deletedId)
						.withContext('deleted calendar id')
						.toBe(expectedId)
					done()
				},
				error: () => {
					done.fail('Failed to delete calendar by id')
				}
			})
		expect(httpClientSpy.delete.calls.count())
			.withContext('one call')
			.toBe(1)
	});


});
