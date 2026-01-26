import { Injectable, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { VehicleDto } from '../dtos/vehicle-dto';
import { WebSocketService } from './websocket-service';
import { VehicleIndicatorDto } from '../dtos/vehicle-indicator-dto';
import { VehicleLocationNotification } from '../notifications/vehicle-location-notification';

@Injectable({
  providedIn: 'root',
})
export class VehicleService {
    private readonly BASE_URL : string = "http://localhost:8080/api/";
    private readonly http = inject(HttpClient);
    private readonly webSocketService : WebSocketService = inject(WebSocketService);

    // Vehicle service handles the vehicle locations and listens to the websocket topic for them. Not sure if this organiztion is ok.
    private vehicleLocationsSubscribtion? : Subscription;

    private vehicleLocations : BehaviorSubject<VehicleIndicatorDto[]> = new BehaviorSubject<VehicleIndicatorDto[]>([])
    readonly vehicleLocations$ = this.vehicleLocations.asObservable();

    constructor() {
      this.webSocketService.connect();
      this.vehicleLocationsSubscribtion = this.webSocketService.vehicleLocations().subscribe({
        next: (notification : VehicleLocationNotification) => {
          if (notification.indicators)
            this.vehicleLocations.next(notification.indicators)
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
