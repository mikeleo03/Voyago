import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateTicketSetupComponent } from './create-ticket-setup.component';

describe('CreateTicketSetupComponent', () => {
  let component: CreateTicketSetupComponent;
  let fixture: ComponentFixture<CreateTicketSetupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTicketSetupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateTicketSetupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
