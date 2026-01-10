import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { DriverRegistrationDto } from '../dtos/driver-registration-dto';
import { VehicleType } from '../enums/vehicle-type';

@Injectable({ providedIn: 'root' })
export class DriverRegistrationService {
  private driverRegistration: DriverRegistrationDto = {
    id: 0,
    avatarUrl: 'default-avatar.jpg',
    email: '',
    password: '',
    name: '',
    surname: '',
    phone: '',
    address: '',
    vehicle: {
      id: 0,
      model: '',
      type: VehicleType.STANDARD,
      seats: 4,
      babyFriendly: false,
      petFriendly: false,
      plates: '',
    },
  };

  getDriverRegistration(): Observable<DriverRegistrationDto> {
    return of(this.driverRegistration);
  }
}
