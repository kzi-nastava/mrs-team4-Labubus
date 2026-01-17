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


  private rides: RideDto[] = [
    {
      id: 1,
      startTime: new Date(),
      endTime: new Date(),
      waypoints: [
        {
          id: 1,
          label: 'Narodnog fronta',
          latitude: 19.45,
          longitude: 48.21
        },
        {
          id: 2,
          label: 'Bulevar oslobodjenja',
          latitude: 19.45,
          longitude: 48.21
        },
        {
          id: 3,
          label: 'Bulevar despota Stefana',
          latitude: 19.45,
          longitude: 48.21
        }
      ],
      driver: {
        email: 'pera@peric.com',
        name: 'Pera',
        surname: 'Peric',
        avatarUrl: '',
        role: Role.DRIVER,
        id: 1,
        phone: "1251323523",
        address: "Test adress 123"
      },
      vehicle: { model: 'Toyota Carolla 2021', type: VehicleType.STANDARD, id: 1, seats: 5, babyFriendly: true, petFriendly: false, plates: "123123123" },
      passengers: [
        {
          email: 'mika@mikic.com',
          name: 'Mika',
          surname: 'Mikic',
          avatarUrl: '',
          role: Role.REGISTERED_USER,
          id: 1,
          phone: "1251323523",
          address: "Test adress 123"
        },
        {
          email: 'djura@djuric.com',
          name: 'Djura',
          surname: 'Djuric',
          avatarUrl: '',
          role: Role.REGISTERED_USER,
          id: 1,
          phone: "1251323523",
          address: "Test adress 123"
        },
      ],
      price: 16.13,
      distance: 10.3,
      panic: false,
      canceledBy: null,
      status: RideStatus.ACCEPTED
    },
    {
      id: 2,
      startTime: new Date(),
      endTime: new Date(),
      waypoints: [
        {
          id: 6,
          label: 'Temerinski put',
          latitude: 19.45,
          longitude: 48.21
        },
        {
          id: 7,
          label: 'Most slobode',
          latitude: 19.45,
          longitude: 48.21
        }
      ],
      driver: {
        email: 'pera@peric.com',
        name: 'Pera',
        surname: 'Peric',
        avatarUrl: '',
        role: Role.DRIVER,
        id: 1,
        phone: "1251323523",
        address: "Test adress 123"
      },
      vehicle: { model: 'Toyota Carolla 2021', type: VehicleType.STANDARD, id: 1, seats: 5, babyFriendly: true, petFriendly: false, plates: "123123123" },
      passengers: [
        {
          email: 'mika@mikic.com',
          name: 'Mika',
          surname: 'Mikic',
          avatarUrl: '',
          role: Role.REGISTERED_USER,
          id: 1,
          phone: "1251323523",
          address: "Test adress 123"
        },
        {
          email: 'djura@djuric.com',
          name: 'Djura',
          surname: 'Djuric',
          avatarUrl: '',
          role: Role.REGISTERED_USER,
          id: 1,
          phone: "1251323523",
          address: "Test adress 123"
        },
      ],
      price: 16.13,
      distance: 10.3,
      panic: false,
      canceledBy: null,
      status: RideStatus.ACCEPTED
    },
    {
      id: 3,
      startTime: new Date(),
      endTime: new Date(),
      waypoints: [
        {
          id: 5,
          label: 'Bulevar cara Lazara',
          latitude: 19.45,
          longitude: 48.21
        },
        {
          id: 3,
          label: 'Bulevar despota Stefana',
          latitude: 19.45,
          longitude: 48.21
        }
      ],
      driver: {
        email: 'pera@peric.com',
        name: 'Pera',
        surname: 'Peric',
        avatarUrl: '',
        role: Role.DRIVER,
        id: 1,
        phone: "1251323523",
        address: "Test adress 123"
      },
      vehicle: { model: 'Toyota Carolla 2021', type: VehicleType.STANDARD, id: 1, seats: 5, babyFriendly: true, petFriendly: false, plates: "123123123" },
      passengers: [
        {
          email: 'mika@mikic.com',
          name: 'Mika',
          surname: 'Mikic',
          avatarUrl: '',
          role: Role.REGISTERED_USER,
          id: 1,
          phone: "1251323523",
          address: "Test adress 123"
        },
        {
          email: 'djura@djuric.com',
          name: 'Djura',
          surname: 'Djuric',
          avatarUrl: '',
          role: Role.REGISTERED_USER,
          id: 1,
          phone: "1251323523",
          address: "Test adress 123"
        },
      ],
      price: 10.74,
      distance: 5.6,
      panic: false,
      canceledBy: 2,
      status: RideStatus.ACCEPTED
    },
    {
      id: 4,
      startTime: new Date(),
      endTime: new Date(),
      waypoints: [
        {
          id: 1,
          label: 'Narodnog fronta',
          latitude: 19.45,
          longitude: 48.21
        },
        {
          id: 3,
          label: 'Bulevar despota Stefana',
          latitude: 19.45,
          longitude: 48.21
        },
        {
          id: 4,
          label: 'Trg mladenaca',
          latitude: 19.45,
          longitude: 48.21
        }
      ],
      driver: {
        email: 'pera@peric.com',
        name: 'Pera',
        surname: 'Peric',
        avatarUrl: '',
        role: Role.DRIVER,
        id: 1,
        phone: "1251323523",
        address: "Test adress 123"
      },
      vehicle: { model: 'Toyota Carolla 2021', type: VehicleType.STANDARD, id: 1, seats: 5, babyFriendly: true, petFriendly: false, plates: "123123123" },
      passengers: [
        {
          email: 'mika@mikic.com',
          name: 'Mika',
          surname: 'Mikic',
          avatarUrl: '',
          role: Role.REGISTERED_USER,
          id: 2,
          phone: "1251323523",
          address: "Test adress 123"
        },
        {
          email: 'djura@djuric.com',
          name: 'Djura',
          surname: 'Djuric',
          avatarUrl: '',
          role: Role.REGISTERED_USER,
          id: 3,
          phone: "1251323523",
          address: "Test adress 123"
        },
      ],
      price: 20.84,
      distance: 17.1,
      panic: true,
      canceledBy: 2,
      status: RideStatus.ACCEPTED
    }
  ];

  private history: BehaviorSubject<RideCardDto[]> = new BehaviorSubject<RideCardDto[]>([]);

  private favorites: BehaviorSubject<RideCardDto[]> = new BehaviorSubject<RideCardDto[]>([]);

  private currentUser : UserDto = {
        email: '',
        name: '',
        surname: '',
        avatarUrl: '',
        role: Role.GUEST,
        id: 0,
        phone: "",
        address: ""
      };

  fetchHistory(query : RideQueryDto, skip : number = 0, count : number = 10) : void {
    const params : HttpParams = this.extractParams(query, skip, count);
    let userId : number = query.userId ?? this.currentUser.id;
    console.log(this.http.get<RideCardDto[]>(`${this.BASE_URL}ride/history/5`, {params}).subscribe((value : RideCardDto[]) => {
      this.history.next(value);
    }))
  }

  clearHistory() {
    this.history.next([]);
  }


  subscribeToHistory(onChange : (cards: RideCardDto[]) => void): void {
    this.history.subscribe(onChange);
  }

  getRide(id: number): Observable<RideDto | undefined> {
    let ride: RideDto | undefined = this.rides.filter(r => r.id == id).length == 0 ? undefined : this.rides.filter(r => r.id == id)[0];
    return of(ride);
  }

  fetchFavorites(query : RideQueryDto, skip : number = 0, count : number = 10) : void {
    const params : HttpParams = this.extractParams(query, skip, count);
    let userId : number = this.currentUser.id;
    this.http.get<RideCardDto[]>(`${this.BASE_URL}ride/${userId}/favorites`, {params})
  }

  clearFavorites() {
    this.favorites.next([]);
  }

  subscribeToFavorites(onChange : (cards: RideCardDto[]) => void): void {
    this.favorites.subscribe(onChange);
  }

  toggleFavorite(userId: number, id: number): void {
    
  }

  private extractParams(query : RideQueryDto, skip : number, count : number) : HttpParams {
    const params : HttpParams = new HttpParams();

    if (query.sortBy != "" && query.sortBy != null)
      params.set('sortBy', query.sortBy.valueOf());

    if (query.sortBy != "" && query.sortBy != null && query.ascending != null)
      params.set('ascending', query.ascending);

    if (query.date != null)
      params.set('date', query.date.toISOString().slice(0, 10) + 'T00:00:00');

    params.set('skip', skip)
    params.set('count', count)

    return params;
  }

  ngOnInit() {
    this.userService.getCurrentUser().subscribe((user : UserDto) => {
      this.currentUser = user;
    })
  }
}
