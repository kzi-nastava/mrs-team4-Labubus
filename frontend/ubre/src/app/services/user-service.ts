import { inject, Injectable } from '@angular/core';
import { UserDto } from '../dtos/user-dto';
import { Role } from '../enums/role';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { UserStatsDto } from '../dtos/user-stats-dto';
import { VehicleDto } from '../dtos/vehicle-dto';
import { VehicleType } from '../enums/vehicle-type';
import { DriverRegistrationDto } from '../dtos/driver-registration-dto';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private currentUser: UserDto = {
    email: 'mika@mikic.com',
    name: 'Mika',
    surname: 'Mikic',
    avatarUrl: 'default-avatar.jpg',
    role: Role.ADMIN,
    id: 2,
    phone: "1251323523",
    address: "Test adress 123"
  };

  private currentUserStats : UserStatsDto = {
    userId: 2,
    activePast24Hours: 500,
    numberOfRides: 15,
    distanceTravelled: 1200,
    moneySpent: 300,
    moneyEarned: 150
  };

  private currentUserVehicle : VehicleDto = {
    id: 1,
    model: "Toyota Prius",
    type: VehicleType.STANDARD,
    seats: 4,
    babyFriendly: true,
    petFriendly: false,
    plates: "BG1234AB",
  };

  private driverRegistration : DriverRegistrationDto = {
    id : 0,
    avatarUrl : 'default-avatar.jpg',
    email : '',
    password : '',
    name : '',
    surname : '',
    phone : '',
    address : '',

    vehicle : {
      id: 0,
      model: "",
      type: VehicleType.STANDARD,
      seats: 4,
      babyFriendly: false,
      petFriendly: false,
      plates: "",
    }
  };

  private readonly http = inject(HttpClient);

  getCurrentUser() : Observable<UserDto> {
    return of(this.currentUser);
  }

  getUserStats(userId : number) : Observable<UserStatsDto> {
    return of(this.currentUserStats);
  }

  getUserVehicle(userId : number) : Observable<VehicleDto> {
    return of(this.currentUserVehicle);
  }

  getDriverRegistration() : Observable<DriverRegistrationDto> {
    return of(this.driverRegistration);
  }
}
