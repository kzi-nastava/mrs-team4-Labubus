import { Component, EventEmitter, Output } from '@angular/core';
import { VehicleType } from '../../../enums/vehicle-type';
import { RideOptionsDto } from '../../../dtos/ride-options-dto';

@Component({
  selector: 'app-ride-options',
  standalone: true,
  imports: [],
  templateUrl: './ride-options.html',
  styleUrl: './ride-options.css',
})
export class RideOptions {

  rideOptions : RideOptionsDto = {
    vehicleType: VehicleType.STANDARD,
    babyFriendly: false,
    petFriendly: false,
  };

  readonly VehicleType = VehicleType;

  @Output() back = new EventEmitter<void>();
  @Output() scheduleRide = new EventEmitter<void>();
  @Output() proceed = new EventEmitter<RideOptionsDto>();

  setRideType(type: VehicleType) {
    this.rideOptions.vehicleType = type;
  }

  toggleRideBaby() {
    this.rideOptions.babyFriendly = !this.rideOptions.babyFriendly;
  }

  toggleRidePet() {
    this.rideOptions.petFriendly = !this.rideOptions.petFriendly;
  }

  onBack() {
    this.back.emit();
  }

  onScheduleRide() {
    this.scheduleRide.emit();
  }

  onProceed() {
    this.proceed.emit(this.rideOptions);
  }
}
