/*
    PURPOSE OF THIS SERVICE IS TO HANDLE THE ROUTING PROCESS, 
    I.E. GETTING THE ROUTE INFO FROM THE API.
    ROTUE(WAYPOINTS) -> ROUTE INFO, HTTP REQUESTS ONLY.
*/

import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map, Observable } from "rxjs";
import { OsrmRouteResponse, RouteInfo } from "./ride-types";
import { WaypointDto } from "../../dtos/waypoint-dto";

@Injectable({ providedIn: 'root' })
export class RoutingService {
    private readonly http = inject(HttpClient);

    route(waypoints: WaypointDto[]): Observable<RouteInfo> {
        const coords = waypoints.map(w => `${w.longitude},${w.latitude}`).join(';');

        const url =
          `https://routing.openstreetmap.de/routed-car/route/v1/driving/${coords}` +
          `?overview=full&geometries=geojson&steps=false`;

        return this.http.get<OsrmRouteResponse>(url).pipe(
            map((response) => {
                const r = response?.routes?.[0]; // first optimal route is taken from the response
                return {
                    distance: r?.distance ?? 0,
                    duration: r?.duration ?? 0,
                    geometry: (r?.geometry as GeoJSON.LineString) ?? { type: 'LineString', coordinates: [] }, // default geometry is an empty line string
                };
            })
        );
    }

    
}
