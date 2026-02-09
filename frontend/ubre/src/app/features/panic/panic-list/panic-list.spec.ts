import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanicList } from './panic-list';

describe('PanicList', () => {
  let component: PanicList;
  let fixture: ComponentFixture<PanicList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanicList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanicList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
