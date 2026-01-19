import { Component, EventEmitter, Output } from '@angular/core';
import { VehicleType } from '../../../enums/vehicle-type';

@Component({
  selector: 'app-ride-options',
  standalone: true,
  imports: [],
  templateUrl: './ride-options.html',
  styleUrl: './ride-options.css',
})
export class RideOptions {

  rideOptions = {
    rideType: VehicleType.STANDARD,
    babyFriendly: false,
    petFriendly: false,
  };

  readonly VehicleType = VehicleType;

  @Output() back = new EventEmitter<void>();
  @Output() scheduleRide = new EventEmitter<void>();
  @Output() proceed = new EventEmitter<{ rideType: VehicleType; babyFriendly: boolean; petFriendly: boolean }>();

  setRideType(type: VehicleType) {
    this.rideOptions.rideType = type;
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
    this.proceed.emit({
      rideType: this.rideOptions.rideType,
      babyFriendly: this.rideOptions.babyFriendly,
      petFriendly: this.rideOptions.petFriendly,
    });
  }
}
