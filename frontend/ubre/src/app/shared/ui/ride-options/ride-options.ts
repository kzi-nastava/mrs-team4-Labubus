import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-ride-options',
  standalone: true,
  imports: [],
  templateUrl: './ride-options.html',
  styleUrl: './ride-options.css',
})
export class RideOptions {

  rideOptions = {
    rideType: 'Standard' as 'Standard' | 'Luxury' | 'Van',
    babyFriendly: false,
    petFriendly: false,
  };

  @Output() back = new EventEmitter<void>();
  @Output() scheduleRide = new EventEmitter<void>();
  @Output() proceed = new EventEmitter<{ rideType: 'Standard' | 'Luxury' | 'Van'; babyFriendly: boolean; petFriendly: boolean }>();

  setRideType(type: 'Standard' | 'Luxury' | 'Van') {
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
