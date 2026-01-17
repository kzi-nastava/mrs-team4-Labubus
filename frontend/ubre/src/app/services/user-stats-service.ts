// service for user stats (used in user layout) 
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserStatsDto } from '../dtos/user-stats-dto';

@Injectable({ providedIn: 'root' })
export class UserStatsService {
    private readonly http = inject(HttpClient);
    private readonly api = 'http://localhost:8080/api';
    
    private readonly currentUserStatsSubject = new BehaviorSubject<UserStatsDto>({ userId: 0, activePast24Hours: 0, numberOfRides: 0, distanceTravelled: 0, moneySpent: 0, moneyEarned: 0 });
    readonly currentUserStats$ = this.currentUserStatsSubject.asObservable();

    setCurrentUserStats(stats: UserStatsDto) {
        this.currentUserStatsSubject.next(stats);
    }

    getUserStats(userId: number): Observable<UserStatsDto> {
        return this.http.get<UserStatsDto>(`${this.api}/users/${userId}/stats`);
    }
}