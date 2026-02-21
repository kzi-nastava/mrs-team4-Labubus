import { Injectable, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { BehaviorSubject, Observable, Subscription, take } from 'rxjs';
import { VehicleDto } from '../dtos/vehicle-dto';
import { WebSocketService } from './websocket-service';
import { VehicleIndicatorDto } from '../dtos/vehicle-indicator-dto';
import { VehicleLocationNotification } from '../notifications/vehicle-location-notification';
import { RideTrackingStore } from './ride-planning/ride-tracking-store';
import { RidePlanningStore } from './ride-planning/ride-planning-store';
import { RideStatus } from '../enums/ride-status';
import { WaypointDto } from '../dtos/waypoint-dto';
import { RideDto } from '../dtos/ride-dto';
import { UserService } from './user-service';
import { UserDto } from '../dtos/user-dto';
import { Role } from '../enums/role';
import { RouteInfo } from './ride-planning/ride-types';

@Injectable({
  providedIn: 'root',
})
export class VehicleService {
  
    private readonly BASE_URL : string = "http://ubre-api.notixdms.com:8080/api/";
    private readonly http = inject(HttpClient);
    private readonly webSocketService : WebSocketService = inject(WebSocketService);
    private readonly rideTrackingStore : RideTrackingStore = inject(RideTrackingStore);
    private readonly ridePlanningStore : RidePlanningStore = inject(RidePlanningStore);
    private readonly userService : UserService = inject(UserService);

    // Vehicle service handles the vehicle locations and listens to the websocket topic for them. Not sure if this organiztion is ok.
    private vehicleLocationsSubscribtion? : Subscription;

    private vehicleLocations : BehaviorSubject<VehicleIndicatorDto[]> = new BehaviorSubject<VehicleIndicatorDto[]>([])
    readonly vehicleLocations$ = this.vehicleLocations.asObservable();

    private locationSendInterval : number | null = null;
    private currentDriverVehicle : VehicleDto | null = null;

    constructor() {
      this.webSocketService.connect();
      this.vehicleLocationsSubscribtion = this.webSocketService.vehicleLocations().subscribe({
        next: (notification : VehicleLocationNotification) => {
          if (notification.indicators) {
            if (this.ridePlanningStore.currentRideSubject$.value != null && this.ridePlanningStore.currentRideSubject$.value.status == RideStatus.IN_PROGRESS) {
              for (let indicator of notification.indicators) {
                if (indicator.driverId == this.ridePlanningStore.currentRideSubject$.value?.driver.id) {
                  (indicator as any).mapCenter = true;
                  for (const i in this.ridePlanningStore.currentRideSubject$.value.waypoints) {
                    const waypoint = this.ridePlanningStore.currentRideSubject$.value.waypoints[i]
                    if (!waypoint.visited) {
                      if (Math.abs(waypoint.longitude - indicator.location.longitude) < 0.00054 && Math.abs(waypoint.latitude - indicator.location.latitude) < 0.00030) {
                        let temp : WaypointDto[] = this.ridePlanningStore.currentRideSubject$.value.waypoints;
                        temp[i].visited = true;
                        this.rideTrackingStore.trackedWaypoints.next(temp);
                      }
                      break
                    }
                  }
                  this.rideTrackingStore.recalculateRideRoute(this.ridePlanningStore.currentRideSubject$.value, indicator.location)
                  break
                }
                delete (indicator as any).mapCenter;
              }
            }
            this.vehicleLocations.next(notification.indicators)
          }
        }
      })

      this.userService.currentUser$.subscribe((currentUser : UserDto) => {
        if (currentUser.role == Role.DRIVER) {
          this.getVehicleByDriver(currentUser.id).pipe(take(1)).subscribe((currentVehicle : VehicleDto) => {
            this.currentDriverVehicle = currentVehicle
            this.locationSendInterval = setInterval(() => {this.sendLocation()}, 1000)
          })
        }
        else {
          this.currentDriverVehicle = null
          if (this.locationSendInterval) {
            clearInterval(this.locationSendInterval)
            this.locationSendInterval = null
          }
        }
      })
    }

    ngOnDestroy() {
      this.vehicleLocationsSubscribtion?.unsubscribe();
      this.webSocketService.disconnect();
    }

    public getVehicle(id : number) : Observable<VehicleDto> {
      return this.http.get<VehicleDto>(`${this.BASE_URL}vehicles/${id}`);
    }

    public getVehicleByDriver(driverId : number) : Observable<VehicleDto> {
      return this.http.get<VehicleDto>(`${this.BASE_URL}vehicles/driver/${driverId}`);
    }

    // ACTUAL SEND LOCATION IMPLEMENTATION (USING DIFFERENT ONE FOR BETTER SHOWCASE)
    // public sendLocation() {
    //   if (!navigator.geolocation) {
    //         return;
    //     }

    //     navigator.geolocation.getCurrentPosition(
    //         (pos) => {
    //             const { latitude, longitude } = pos.coords;
    //             const id = Date.now();
    //             const fallback = `${latitude.toFixed(5)}, ${longitude.toFixed(5)}`;

    //             const newWaypoint: WaypointDto = {
    //                 id: Number(id),
    //                 label: fallback,
    //                 latitude: latitude,
    //                 longitude: longitude,
    //                 visited: false
    //             };

    //             return this.http.post<VehicleDto>(`${this.BASE_URL}vehicles/${this.currentDriverVehicle?.id}/location`, newWaypoint).pipe(take(1)).subscribe(()=>{});
    //         },
    //         (error) => {
    //             console.error('Error getting current location:', error);
    //         }
    //     );
    // }

    public sendLocation() {
      this.ridePlanningStore.currentRide$.pipe(take(1)).subscribe((currentRide : RideDto | null) => {
        if (currentRide == null || currentRide.status != RideStatus.IN_PROGRESS)
          return;

        this.rideTrackingStore.trackedRoute$.pipe(take(1)).subscribe((route : RouteInfo) => {
          if (route.geometry.coordinates.length < 2)
            return;

          let coordinate = route.geometry.coordinates[0]
          for (let c of route.geometry.coordinates) {
            if (c[0] != coordinate[0] || c[1] != coordinate[1]) {
              coordinate = c;
              break;
            }
          }

          if (route.geometry.coordinates[0][0] == coordinate[0] && route.geometry.coordinates[0][1] == coordinate[1])
            return

          const id = Date.now();
          const newWaypoint: WaypointDto = {
              id: Number(id),
              label: "",
              latitude: coordinate[1],
              longitude: coordinate[0],
              visited: false
          };
          return this.http.post<VehicleDto>(`${this.BASE_URL}vehicles/${this.currentDriverVehicle?.id}/location`, newWaypoint).pipe(take(1)).subscribe(()=>{});
        })
      })
    }
}
