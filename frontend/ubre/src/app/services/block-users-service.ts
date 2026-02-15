import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDto } from '../dtos/user-dto';

/**
 * Backend endpoints expected by this service:
 *
 * GET  /api/users (admin)  -> UserDto[]  (list all users for block management)
 * PUT  /api/users/{id}/block   (body optional: { note?: string }) -> 200
 * PUT  /api/users/{id}/unblock -> 200
 */
@Injectable({ providedIn: 'root' })
export class BlockUsersService {
  private readonly http = inject(HttpClient);
  private readonly api = 'http://localhost:8080/api';

  getUsers(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.api}/users/get-all`);
  }

  blockUser(userId: number, note?: string): Observable<void> {
    return this.http.put<void>(`${this.api}/users/${userId}/block`, note != null ? { note } : {});
  }

  unblockUser(userId: number): Observable<void> {
    return this.http.put<void>(`${this.api}/users/${userId}/unblock`, {});
  }
}
