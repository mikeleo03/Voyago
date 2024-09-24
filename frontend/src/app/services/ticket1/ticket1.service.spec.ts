import { TestBed } from '@angular/core/testing';

import { Ticket1Service } from './ticket1.service';

describe('Ticket1Service', () => {
  let service: Ticket1Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Ticket1Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
