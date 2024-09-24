import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketDetailAdminComponent } from './ticket-detail-admin.component';

describe('TicketDetailAdminComponent', () => {
  let component: TicketDetailAdminComponent;
  let fixture: ComponentFixture<TicketDetailAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketDetailAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketDetailAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
