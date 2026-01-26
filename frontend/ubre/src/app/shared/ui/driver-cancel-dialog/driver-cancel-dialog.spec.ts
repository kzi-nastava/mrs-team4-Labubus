import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverCancelDialog } from './driver-cancel-dialog';

describe('DriverCancelDialog', () => {
  let component: DriverCancelDialog;
  let fixture: ComponentFixture<DriverCancelDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverCancelDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverCancelDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
