import { Injectable, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { RideCardDto } from '../dtos/ride-card-dto';
import { RideDto } from '../dtos/ride-dto';
import { RideStatus } from '../enums/ride-status';
import { VehicleType } from '../enums/vehicle_type';
import { Role } from '../enums/role';

@Injectable({
  providedIn: 'root',
})
export class RideService {
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

  private cards: RideCardDto[] = [
    {
      rideId: 1,
      startTime: new Date(),
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
      favorite: true
    },
    {
      rideId: 2,
      startTime: new Date(),
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
      favorite: false
    },
    {
      rideId: 3,
      startTime: new Date(),
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
      favorite: false
    },
    {
      rideId: 4,
      startTime: new Date(),
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
      favorite: false
    },
  ];

  private favorite: RideCardDto[] = [
    {
      rideId: 1,
      startTime: new Date(),
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
      favorite: true
    }
  ];

  private readonly http = inject(HttpClient);

  getHistory(): Observable<RideCardDto[]> {
    return of(this.cards);
  }

  getRide(id: number): Observable<RideDto | undefined> {
    let ride: RideDto | undefined = this.rides.filter(r => r.id == id).length == 0 ? undefined : this.rides.filter(r => r.id == id)[0];
    return of(ride);
  }

  getFavorite(userId: number): Observable<RideCardDto[]> {
    return of(this.favorite);
  }

  toggleFavorite(userId: number, id: number): void {
    let card : RideCardDto | undefined = this.cards.filter(r => r.rideId == id).length == 0 ? undefined : this.cards.filter(r => r.rideId == id)[0];
    if (card !== undefined) {
      card.favorite = !card.favorite
      this.cards.map(c => {if (c.rideId == id) return {...c, favorite: !c.favorite}; return c})
      if (card.favorite == true)
        this.favorite.push(card);
      else
        this.favorite = this.favorite.filter((c) => c.rideId == card.rideId);
    }
  }
}
