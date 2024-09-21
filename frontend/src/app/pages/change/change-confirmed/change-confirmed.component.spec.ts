import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeConfirmedComponent } from './change-confirmed.component';

describe('ChangeConfirmedComponent', () => {
  let component: ChangeConfirmedComponent;
  let fixture: ComponentFixture<ChangeConfirmedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeConfirmedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeConfirmedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
