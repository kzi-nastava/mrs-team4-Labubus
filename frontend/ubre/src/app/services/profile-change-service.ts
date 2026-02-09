import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { ProfileChangeDto } from '../dtos/profile-change-dto';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ProfileChangeService {
  private readonly http = inject(HttpClient);
  private readonly api = 'http://localhost:8080/api';
  private readonly profileChangesSubject = new BehaviorSubject<ProfileChangeDto[]>([]);
  readonly profileChanges$ = this.profileChangesSubject.asObservable();

  loadPendingProfileChanges() {
    this.http
      .get<ProfileChangeDto[]>(`${this.api}/drivers/profile-changes/pending`)
      .subscribe(data => this.profileChangesSubject.next(data));
  }

  // accept method (by clicking accept, request is accepted, removed from list and sent to backend)
  approve(id: number) {
    this.http.put<void>(`${this.api}/drivers/profile-changes/${id}/approve`, {}).subscribe(() => {
      this.profileChangesSubject.next(this.profileChangesSubject.value.filter(pc => pc.id !== id));
    });
  }

  // reject method (by clicking reject, request is rejected, removed from list and sent to backend)
  reject(id: number) {
    this.http.put<void>(`${this.api}/drivers/profile-changes/${id}/reject`, {}).subscribe(() => {
      this.profileChangesSubject.next(this.profileChangesSubject.value.filter(pc => pc.id !== id));
    });
  }
}
