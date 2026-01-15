import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { UserDto } from '../dtos/user-dto';
import { UserStatsDto } from '../dtos/user-stats-dto';
import { VehicleDto } from '../dtos/vehicle-dto';
import { Role } from '../enums/role';
import { VehicleType } from '../enums/vehicle-type';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly api = 'http://localhost:8080/api';

  private readonly currentUserSubject = new BehaviorSubject<UserDto>({ email: '', name: 'Guest', surname: '', avatarUrl: '', role: Role.GUEST, id: 0, phone: '', address: '' });
  readonly currentUser$ = this.currentUserSubject.asObservable();

  private readonly avatarSrcSubject = new BehaviorSubject<string>('default-avatar.jpg');
  readonly avatarSrc$ = this.avatarSrcSubject.asObservable();


  // mock podaci (privremeno)
  private currentUserStats: UserStatsDto = {
    userId: 2,
    activePast24Hours: 500,
    numberOfRides: 15,
    distanceTravelled: 1200,
    moneySpent: 300,
    moneyEarned: 150,
  };

  private currentUserVehicle: VehicleDto = {
    id: 1,
    model: 'Toyota Prius',
    type: VehicleType.STANDARD,
    seats: 4,
    babyFriendly: true,
    petFriendly: false,
    plates: 'BG1234AB',
  };

  // --- API ---
  getUserById(userId: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.api}/users/${userId}`);
  }

  getUserAvatar(userId: number): Observable<Blob> {
    return this.http.get(`${this.api}/users/${userId}/avatar`, { responseType: 'blob' });
  }

  // --- actions ---
  setCurrentUserById(id: number) {
    this.getUserById(id).subscribe({
      next: user => {
        this.currentUserSubject.next(user);
        this.loadAvatar(user.id); // automatski učitaj avatar kad se postavi user
      },
      error: err => {
        if (err.status === 404) alert('User not found');
      },
    });
  }

  loadAvatar(userId: number) {
    this.getUserAvatar(userId).subscribe({
      next: blob => this.avatarSrcSubject.next(URL.createObjectURL(blob)),
      error: () => this.avatarSrcSubject.next('default-avatar.jpg'),
    });
  }

  // --- mock (privremeno) ---
  getUserStats(userId: number): Observable<UserStatsDto> {
    return of({ ...this.currentUserStats, userId });
  }

  getUserVehicle(userId: number): Observable<VehicleDto> {
    return of(this.currentUserVehicle);
  }




  // GETTER ZA RIDE HISTORY (MOŽE DA SE IZBACI - ne diram ništa sam)
  getCurrentUser(): Observable<UserDto> {
    return this.currentUser$;
  }
}
