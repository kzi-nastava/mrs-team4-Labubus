import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisteredUser } from './registered-user';

describe('RegisteredUser', () => {
  let component: RegisteredUser;
  let fixture: ComponentFixture<RegisteredUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisteredUser]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisteredUser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
