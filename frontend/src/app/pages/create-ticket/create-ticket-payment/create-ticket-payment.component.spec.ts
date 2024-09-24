import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateTicketPaymentComponent } from './create-ticket-payment.component';

describe('CreateTicketPaymentComponent', () => {
  let component: CreateTicketPaymentComponent;
  let fixture: ComponentFixture<CreateTicketPaymentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTicketPaymentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateTicketPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
