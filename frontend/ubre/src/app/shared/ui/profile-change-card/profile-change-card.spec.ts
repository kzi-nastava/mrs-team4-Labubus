import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileChangeCard } from './profile-change-card';

describe('ProfileChangeCard', () => {
  let component: ProfileChangeCard;
  let fixture: ComponentFixture<ProfileChangeCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileChangeCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileChangeCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
