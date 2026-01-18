// a service for fetching user statistics on demand
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserStatsDto } from '../dtos/user-stats-dto';
import { UserService } from './user-service';

@Injectable({ providedIn: 'root' })
export class UserStatsService {
    private readonly http = inject(HttpClient);
    private readonly userService = inject(UserService);
    private readonly api = 'http://localhost:8080/api';

    // user stats for current user, subject and observable
    private readonly currentUserStatsSubject = new BehaviorSubject<UserStatsDto>({ userId: 0, activePast24Hours: 0, numberOfRides: 0, distanceTravelled: 0, moneySpent: 0, moneyEarned: 0 });
    readonly currentUserStats$ = this.currentUserStatsSubject.asObservable();

    getUserStats(): Observable<UserStatsDto> {
        return this.http.get<UserStatsDto>(`${this.api}/users/${this.userService.getCurrentUserId()}/stats`);
    }

    setCurrentUserStats(stats: UserStatsDto) {
        this.currentUserStatsSubject.next(stats);
    }    
}