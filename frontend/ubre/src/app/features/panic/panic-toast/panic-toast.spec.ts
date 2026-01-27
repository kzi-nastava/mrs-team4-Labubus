import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanicToast } from './panic-toast';

describe('PanicToast', () => {
  let component: PanicToast;
  let fixture: ComponentFixture<PanicToast>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanicToast]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanicToast);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
