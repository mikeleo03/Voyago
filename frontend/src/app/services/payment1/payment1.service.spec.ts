import { TestBed } from '@angular/core/testing';

import { Payment1Service } from './payment1.service';

describe('Payment1Service', () => {
  let service: Payment1Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Payment1Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
