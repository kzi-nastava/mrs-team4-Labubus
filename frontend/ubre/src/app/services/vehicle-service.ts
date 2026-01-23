import { Injectable, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { Observable } from 'rxjs';
import { VehicleDto } from '../dtos/vehicle-dto';

@Injectable({
  providedIn: 'root',
})
export class VehicleService {
    private readonly BASE_URL : string = "http://localhost:8080/api/";
    private readonly http = inject(HttpClient);

    public getVehicle(id : number) : Observable<VehicleDto> {
      return this.http.get<VehicleDto>(`${this.BASE_URL}vehicles/${id}`);
    }

    public getVehicleByDriver(driverId : number) : Observable<VehicleDto> {
      return this.http.get<VehicleDto>(`${this.BASE_URL}vehicles/driver/${driverId}`);
    }
}
