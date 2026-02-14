import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideHistoryReviews } from './ride-history-reviews';

describe('RideHistoryReviews', () => {
  let component: RideHistoryReviews;
  let fixture: ComponentFixture<RideHistoryReviews>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryReviews]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistoryReviews);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
