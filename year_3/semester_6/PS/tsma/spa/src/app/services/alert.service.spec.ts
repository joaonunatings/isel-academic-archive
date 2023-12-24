import {TestBed} from '@angular/core/testing';

import {AlertService} from './alert.service';
import {Alert, AlertType} from "../components/alert/alert.component";

describe('AlertServiceService', () => {
	let alertService: AlertService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		alertService = TestBed.inject(AlertService);
	});

	it('should be created', () => {
		expect(alertService).toBeTruthy();
	});

	it('should send and receive sent alert object', function (done: DoneFn) {
		const expectedAlert = new Alert()
		expectedAlert.subject = 'Subject'
		expectedAlert.text = 'text'
		expectedAlert.type = AlertType.NOTIFICATION

		alertService.alertToBeShown$
			.subscribe({
				next: (alert) => {
					expect(alert)
						.withContext('received alert')
						.toEqual(expectedAlert)
					done()
				},
				error: () => {
					done.fail('Failed to receive alert')
				}
			})

		alertService.showAlert(expectedAlert)
	});
});
