import { Injectable, inject } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { RideCardDto } from '../dtos/ride-card-dto';
import { RideDto } from '../dtos/ride-dto';
import { RideStatus } from '../enums/ride-status';
import { VehicleType } from '../enums/vehicle-type';
import { Role } from '../enums/role';
import { RideQueryDto } from '../dtos/ride-query';
import { UserService } from './user-service';
import { UserDto } from '../dtos/user-dto';

@Injectable({
  providedIn: 'root',
})
export class RideService {
  private readonly BASE_URL : string = "http://localhost:8080/api/";
  private readonly userService : UserService = inject(UserService);
  private readonly http = inject(HttpClient);


  private history : BehaviorSubject<RideCardDto[]> = new BehaviorSubject<RideCardDto[]>([]);
  public history$ : Observable<RideCardDto[]> = this.history.asObservable();

  private favorites : BehaviorSubject<RideCardDto[]> = new BehaviorSubject<RideCardDto[]>([]);
  public favorites$ : Observable<RideCardDto[]> = this.favorites.asObservable();

  fetchHistory(query : RideQueryDto, skip : number = 0, count : number = 10) : void {
    const queryParams : HttpParams = this.extractParams(query, skip, count);
    this.userService.getCurrentUser().subscribe((currentUser : UserDto) => {
      let userId : number = query.userId ?? currentUser.id;
      if (currentUser.role === Role.ADMIN && query.userId === null)
        this.http.get<RideCardDto[]>(`${this.BASE_URL}rides/history`, {params: queryParams}).subscribe((value : RideCardDto[]) => {
          this.history.next(value);
        })
      else
        this.http.get<RideCardDto[]>(`${this.BASE_URL}rides/history/${userId}`, {params: queryParams}).subscribe((value : RideCardDto[]) => {
          this.history.next(value);
        })
    })
  }

  clearHistory() {
    this.history.next([]);
  }

  getRide(id: number): Observable<RideDto> {
    return this.http.get<RideDto>(`${this.BASE_URL}rides/${id}`);
  }

  fetchFavorites(query : RideQueryDto, skip : number = 0, count : number = 10) : void {
    const params : HttpParams = this.extractParams(query, skip, count);
    this.userService.getCurrentUser().subscribe((currentUser : UserDto) => {
      this.http.get<RideCardDto[]>(`${this.BASE_URL}rides/${currentUser.id}/favorites`, {params})
    })
  }

  clearFavorites() {
    this.favorites.next([]);
  }

  addToFavorites(id: number): void {
    this.userService.getCurrentUser().subscribe((currentUser : UserDto) => {
      this.http.put<void>(`${this.BASE_URL}rides/${currentUser.id}/favorites/${id}`, null).subscribe({
        next: () => {
          let updatedHistory = this.history.value
          updatedHistory = updatedHistory.map((ride : RideCardDto) => {
            if (ride.id == id)
              return { ...ride, favorite: true };
            return ride;
          })
          this.history.next(updatedHistory)
        },
        error: err => {
          if (err.status === 404) alert('Ride not found');
        },
      })
    })
  }

  removeFromFavorites(id: number): void {
    this.userService.getCurrentUser().subscribe((currentUser : UserDto) => {
      this.http.delete<void>(`${this.BASE_URL}rides/${currentUser.id}/favorites/${id}`).subscribe({
        next: () => {
          let updatedHistory = this.history.value
          updatedHistory = updatedHistory.map((ride : RideCardDto) => {
            if (ride.id == id)
              return { ...ride, favorite: false };
            return ride;
          })
          this.history.next(updatedHistory)

          let updatedFavorites = this.favorites.value.filter((ride : RideCardDto) => ride.id != id)
          this.favorites.next(updatedFavorites);
        },
        error: err => {
          if (err.status === 404) alert('Ride not found');
        },
      })
    })
  }

  private extractParams(query : RideQueryDto, skip : number, count : number) : HttpParams {
    let params : HttpParams = new HttpParams();

    if (query.sortBy != "" && query.sortBy != null)
      params = params.set('sortBy', query.sortBy.valueOf());

    if (query.sortBy != "" && query.sortBy != null && query.ascending != null)
      params = params.set('ascending', query.ascending);

    if (query.date != null)
      params = params.set('date', query.date.toISOString().slice(0, 10) + 'T00:00:00');

    params = params.set('skip', skip)
    params = params.set('count', count)

    return params;
  }
}
