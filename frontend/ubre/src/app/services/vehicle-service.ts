import { Injectable, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { VehicleDto } from '../dtos/vehicle-dto';
import { WebSocketService } from './websocket-service';
import { VehicleIndicatorDto } from '../dtos/vehicle-indicator-dto';
import { VehicleLocationNotification } from '../notifications/vehicle-location-notification';
import { RideTrackingStore } from './ride-planning/ride-tracking-store';
import { RidePlanningStore } from './ride-planning/ride-planning-store';
import { RideStatus } from '../enums/ride-status';

@Injectable({
  providedIn: 'root',
})
export class VehicleService {
    private readonly BASE_URL : string = "http://localhost:8080/api/";
    private readonly http = inject(HttpClient);
    private readonly webSocketService : WebSocketService = inject(WebSocketService);
    private readonly rideTrackingStore : RideTrackingStore = inject(RideTrackingStore);
    private readonly ridePlanningStore : RidePlanningStore = inject(RidePlanningStore);

    // Vehicle service handles the vehicle locations and listens to the websocket topic for them. Not sure if this organiztion is ok.
    private vehicleLocationsSubscribtion? : Subscription;

    private vehicleLocations : BehaviorSubject<VehicleIndicatorDto[]> = new BehaviorSubject<VehicleIndicatorDto[]>([])
    readonly vehicleLocations$ = this.vehicleLocations.asObservable();

    constructor() {
      this.webSocketService.connect();
      this.vehicleLocationsSubscribtion = this.webSocketService.vehicleLocations().subscribe({
        next: (notification : VehicleLocationNotification) => {
          if (notification.indicators) {
            if (this.ridePlanningStore.currentRideSubject$.value != null && this.ridePlanningStore.currentRideSubject$.value.status == RideStatus.IN_PROGRESS) {
              for (let indicator of notification.indicators) {
                if (indicator.driverId == this.ridePlanningStore.currentRideSubject$.value?.driver.id) {
                  (indicator as any).mapCenter = true;
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
}
