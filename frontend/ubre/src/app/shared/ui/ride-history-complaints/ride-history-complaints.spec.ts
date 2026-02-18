import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideHistoryComplaints } from './ride-history-complaints';

describe('RideHistoryComplaints', () => {
  let component: RideHistoryComplaints;
  let fixture: ComponentFixture<RideHistoryComplaints>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryComplaints]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistoryComplaints);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
