import { TestBed } from '@angular/core/testing';

import { GraphSyncService } from './graph-sync.service';

describe('GraphSyncService', () => {
  let service: GraphSyncService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GraphSyncService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
