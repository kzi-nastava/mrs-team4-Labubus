// this service is only used to estimate the price of a ride
import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { VehicleType } from "../../enums/vehicle-type";

@Injectable({ providedIn: 'root' })
export class OrderingService {
    private readonly http = inject(HttpClient);
    private readonly api = 'http://localhost:8080/api';

    // we send 0 as standard, 1 as van, 2 as luxury
    estimatePrice(distance: number, vehicleType: VehicleType): Observable<number> {
        const num : number = vehicleType === VehicleType.STANDARD ? 0 : vehicleType === VehicleType.VAN ? 1 : 2;
        return this.http.post<number>(`${this.api}/rides/price-estimate`, { distance, vehicleType: num });
    }
}