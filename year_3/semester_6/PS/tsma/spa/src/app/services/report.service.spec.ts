import { TestBed } from '@angular/core/testing';

import { ReportService } from './report.service';
import {HttpClient} from "@angular/common/http";


describe('ReportService', () => {
	let reportService: ReportService;
	let httpClientSpy: jasmine.SpyObj<HttpClient>

	beforeEach(() => {
		const spy = jasmine.createSpyObj('HttpClient', ['get','post','delete'])

		TestBed.configureTestingModule({
		  providers: [
			  ReportService,
			  { provide: HttpClient, useValue: spy }
		  ]
		});
		reportService = TestBed.inject(ReportService);
		httpClientSpy = TestBed.inject(HttpClient) as jasmine.SpyObj<HttpClient>
	});

	it('should be created', () => {
		expect(reportService).toBeTruthy();
	});
});
