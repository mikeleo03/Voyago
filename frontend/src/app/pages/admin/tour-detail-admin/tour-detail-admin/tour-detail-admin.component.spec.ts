import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TourDetailAdminComponent } from './tour-detail-admin.component';

describe('TourDetailAdminComponent', () => {
  let component: TourDetailAdminComponent;
  let fixture: ComponentFixture<TourDetailAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TourDetailAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TourDetailAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
