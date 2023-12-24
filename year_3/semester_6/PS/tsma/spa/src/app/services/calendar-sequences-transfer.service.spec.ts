import { TestBed } from '@angular/core/testing';

import { CalendarSequencesTransferService } from './calendar-sequences-transfer.service';
import {ShiftSequence} from "../model/dtos/calendar/calendar-create-dto";

describe('CalendarCreationService', () => {
	let sequenceTransferService: CalendarSequencesTransferService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		sequenceTransferService = TestBed.inject(CalendarSequencesTransferService);
	});

	it('should be created', () => {
		expect(sequenceTransferService).toBeTruthy();
	});

	it('expected sequence should be sent and received by the service', function (done: DoneFn) {
		const expectedSequence = [new ShiftSequence(1,[]),new ShiftSequence(2,[])]

		sequenceTransferService.sequences$
			.subscribe({
				next: (sequences) => {
					expect(sequences)
						.withContext('expected sequence')
						.toEqual(expectedSequence)
					done()
				},
				error: () => {
					done.fail('Failed to receive sequences')
				}
			})

		sequenceTransferService.sendSequences(expectedSequence)
	});
});
