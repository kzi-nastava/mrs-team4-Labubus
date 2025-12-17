import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Sheet } from './sheet';

describe('Sheet', () => {
  let component: Sheet;
  let fixture: ComponentFixture<Sheet>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Sheet]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Sheet);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
