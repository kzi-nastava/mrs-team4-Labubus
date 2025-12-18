import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideCard } from './ride-card';

describe('RideCard', () => {
  let component: RideCard;
  let fixture: ComponentFixture<RideCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
