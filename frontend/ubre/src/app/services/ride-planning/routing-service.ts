/*
    PURPOSE OF THIS SERVICE IS TO HANDLE THE ROUTING PROCESS, 
    I.E. GETTING THE ROUTE INFO FROM THE API.
    ROTUE(WAYPOINTS) -> ROUTE INFO, HTTP REQUESTS ONLY.
*/

import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { RouteInfo } from "./ride-types";

@Injectable({ providedIn: 'root' })
export class RoutingService {
    private readonly http = inject(HttpClient);
}
