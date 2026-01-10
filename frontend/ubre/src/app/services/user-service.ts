import { inject, Injectable } from '@angular/core';
import { UserDto } from '../dtos/user-dto';
import { Role } from '../enums/role';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { UserStatsDto } from '../dtos/user-stats-dto';
import { VehicleDto } from '../dtos/vehicle-dto';
import { VehicleType } from '../enums/vehicle-type';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private readonly http = inject(HttpClient);
  private readonly api = 'http://localhost:8080/api';

  private readonly currentUser = new BehaviorSubject<UserDto>({
    email: 'mika@mikic.com',
    name: 'Mika',
    surname: 'Mikic',
    avatarUrl: 'default-avatar.jpg',
    role: Role.ADMIN,
    id: 2,
    phone: '1251323523',
    address: 'Test adress 123',
  });

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

  getCurrentUser() : Observable<UserDto> {
    return this.currentUser.asObservable();
  }

  getUserStats(userId : number) : Observable<UserStatsDto> {
    return of(this.currentUserStats);
  }

  getUserVehicle(userId : number) : Observable<VehicleDto> {
    return of(this.currentUserVehicle);
  }

  getUserById(userId: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.api}/user/${userId}`);
  }

  setCurrentUserById(id: number) {
    this.getUserById(id).subscribe({
      next: user => this.currentUser.next(user),
      error: err => {
        if (err.status === 404) alert('User not found');
      }
    });
  }
}
