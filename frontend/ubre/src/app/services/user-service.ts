import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of, take } from 'rxjs';
import { UserDto } from '../dtos/user-dto';
import { UserStatsDto } from '../dtos/user-stats-dto';
import { VehicleDto } from '../dtos/vehicle-dto';
import { Role } from '../enums/role';
import { VehicleType } from '../enums/vehicle-type';

@Injectable({ providedIn: 'root' })
export class UserService {
  
  private readonly http = inject(HttpClient);
  private readonly api = 'http://localhost:8080/api';

  private readonly currentUserSubject = new BehaviorSubject<UserDto>({ email: '', name: 'Guest', surname: '', avatarUrl: 'default-avatar.jpg', role: Role.GUEST, id: 0, phone: '', address: '', isBlocked: false });
  readonly currentUser$ = this.currentUserSubject.asObservable();

  private readonly avatarSrcSubject = new BehaviorSubject<string>('default-avatar.jpg');
  readonly avatarSrc$ = this.avatarSrcSubject.asObservable();

  private readonly blockNoteSubject = new BehaviorSubject<string | null>(null);
  readonly currentUserBlockNote$ = this.blockNoteSubject.asObservable();

  private currentUserVehicleSubject = new BehaviorSubject<VehicleDto>({ id: 0, model: '', type: VehicleType.STANDARD, seats: 0, babyFriendly: false, petFriendly: false, plates: '' });
  readonly currentUserVehicle$ = this.currentUserVehicleSubject.asObservable();


  // mock podaci (privremeno)
  private currentUserStats: UserStatsDto = {
    userId: 2,
    activePast24Hours: 500,
    numberOfRides: 15,
    distanceTravelled: 1200,
    moneySpent: 300,
    moneyEarned: 150,
  };

  // --- API ---
  getUserById(userId: number): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.api}/users/${userId}`);
  }

  getUserAvatar(userId: number): Observable<Blob> {
    return this.http.get(`${this.api}/users/${userId}/avatar`, { responseType: 'blob' });
  }

  getVehicleByDriver(driverId: number): Observable<VehicleDto> {
    return this.http.get<VehicleDto>(`${this.api}/vehicles/driver/${driverId}`);
  }

  /** GET /users/{id}/block-note — text/plain; prazan string ako nema beleške */
  getBlockNote(userId: number): Observable<string> {
    return this.http.get(`${this.api}/users/${userId}/block-note`, { responseType: 'text' });
  }



  // --- actions ---
  setCurrentUserById(id: number) {
    if (id === 0 || id === null) {
      this.currentUserSubject.next({ id: 0, role: Role.GUEST, name: 'Guest', surname: '', email: '', avatarUrl: 'default-avatar.jpg', phone: '', address: '', isBlocked: false });
      this.blockNoteSubject.next(null);
      return;
    }
    this.getUserById(id).pipe(take(1)).subscribe({
      next: user => {
        this.currentUserSubject.next(user);
        this.loadAvatar(user.id);
        if (user.role === Role.DRIVER) {
          this.loadUserVehicle(user.id);
        }
        if (user.isBlocked) {
          this.getBlockNote(user.id).pipe(take(1)).subscribe({
            next: text => this.blockNoteSubject.next((text?.trim() || '') ? text.trim() : 'There is no reason provided'),
            error: () => this.blockNoteSubject.next('There is no reason provided'),
          });
        } else {
          this.blockNoteSubject.next(null);
        }
      },
      error: err => {
        if (err.status === 404) alert('User not found');
      },
    });
  }

  loadAvatar(userId: number) {
    this.getUserAvatar(userId).pipe(take(1)).subscribe({
      next: blob => this.avatarSrcSubject.next(URL.createObjectURL(blob)),
      error: () => this.avatarSrcSubject.next('default-avatar.jpg'),
    });
  }

  loadUserVehicle(userId: number) {
    this.getVehicleByDriver(userId).pipe(take(1)).subscribe({
      next: vehicle => this.currentUserVehicleSubject.next(vehicle),
      error: err => {
        if (err.status === 404) alert('Vehicle not found');
      },
    });
  }

  // --- mock (privremeno) ---
  getUserStats(userId: number): Observable<UserStatsDto> {
    return of({ ...this.currentUserStats, userId });
  }


  // GETTER ZA RIDE HISTORY (MOŽE DA SE IZBACI - ne diram ništa sam)
  getCurrentUser(): Observable<UserDto> {
    return this.currentUser$
  }


  // get current user id
  getCurrentUserId(): number {
    return this.currentUserSubject.value.id;
  }

  resetAvatar() {
    this.avatarSrcSubject.next('default-avatar.jpg');
  }
  
  getUsersByFullName(fullName : string) : Observable<UserDto[]> {
    const params : HttpParams = new HttpParams().set("fullName", fullName);
    return this.http.get<UserDto[]>(`${this.api}/users`, {params: params});
  }
}
