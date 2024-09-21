import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomersAdminComponent } from './customers-admin.component';

describe('CustomersAdminComponent', () => {
  let component: CustomersAdminComponent;
  let fixture: ComponentFixture<CustomersAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomersAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomersAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
