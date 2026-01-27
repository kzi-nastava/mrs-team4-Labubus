import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { RideService } from '../../../services/ride-service';
import { PanicDto } from '../../../dtos/panic-dto';
import { DatePipe } from '@angular/common';
@Component({
  selector: 'app-panic-list',
  imports: [DatePipe],
  templateUrl: './panic-list.html',
  styleUrl: './panic-list.css',
})
export class PanicList implements OnInit {
  panics: PanicDto[] = [];

  readonly CAR_ICON = 'directions_car_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg';
  readonly USER_ICON = 'person_add_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg';
  readonly ALERT_ICON = 'warning-primary-text.svg';

  constructor(private rideService: RideService) {}

  ngOnInit(): void {
    this.rideService.getPanics().subscribe(data => {
      this.panics = data;
    });
  }
}
