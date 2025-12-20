import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteTable } from './route-table';

describe('RouteTable', () => {
  let component: RouteTable;
  let fixture: ComponentFixture<RouteTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouteTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RouteTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
