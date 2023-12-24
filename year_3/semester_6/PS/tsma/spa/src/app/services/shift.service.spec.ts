import {TestBed} from '@angular/core/testing';

import {ShiftService} from './shift.service';
import {HttpClient} from "@angular/common/http";
import {Shift} from "../model/shift";
import {Type} from "../model/type";
import {of} from "rxjs";


describe('ShiftService', () => {
	let shiftService: ShiftService;
	let httpClientSpy: jasmine.SpyObj<HttpClient>

	beforeEach(() => {
		const spy = jasmine.createSpyObj('HttpClient', ['get','patch'])
		TestBed.configureTestingModule({
			providers: [
				ShiftService,
				{ provide: HttpClient, useValue: spy }
			]
		});
		shiftService = TestBed.inject(ShiftService);
		httpClientSpy = TestBed.inject(HttpClient) as jasmine.SpyObj<HttpClient>
	});

	it('should be created', () => {
		expect(shiftService).toBeTruthy();
	});

});
