import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComplaintModal } from './complaint-modal';

describe('ComplaintModal', () => {
  let component: ComplaintModal;
  let fixture: ComponentFixture<ComplaintModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComplaintModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComplaintModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
