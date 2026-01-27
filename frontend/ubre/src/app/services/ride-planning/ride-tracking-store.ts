import { inject, Injectable } from "@angular/core";
import { RoutingService } from "./routing-service";
import { BehaviorSubject, Observable } from "rxjs";
import { WaypointDto } from "../../dtos/waypoint-dto";
import { RidePlanningStore } from "./ride-planning-store";
import { RideDto } from "../../dtos/ride-dto";
import { take } from "rxjs";
import { RideStatus } from "../../enums/ride-status";
import { RouteInfo } from "./ride-types";

@Injectable({ providedIn: 'root' })
export class RideTrackingStore {
    private routingService = inject(RoutingService);
    private ridePlanningStore = inject(RidePlanningStore);

    private trackedRoute : BehaviorSubject<RouteInfo> = new BehaviorSubject<RouteInfo>({
        distance: 0,
        duration: 0,
        geometry: {
            type: 'LineString',
            coordinates: []
        }
    });
    public readonly trackedRoute$ = this.trackedRoute.asObservable();
    private trackedWaypoints : BehaviorSubject<WaypointDto[]> = new BehaviorSubject<WaypointDto[]>([]);
    public readonly trackedWaypoints$ = this.trackedWaypoints.asObservable();

    constructor() {
        this.ridePlanningStore.currentRide$.subscribe((ride : RideDto | null) => {
            if (ride == null || ride.status != RideStatus.IN_PROGRESS) {
                this.trackedRoute.next({
                    distance: 0,
                    duration: 0,
                    geometry: {
                        type: 'LineString',
                        coordinates: []
                    }
                })
                this.trackedWaypoints.next([])
            }
            else
                this.recalculateRideRoute(ride)
        })
    }

    public recalculateRideRoute(ride : RideDto, currentLocation : WaypointDto | undefined = undefined) {
        let coordinates = ride.waypoints;
        if (currentLocation != undefined)
            coordinates = [currentLocation, ...coordinates]

        this.routingService.route(coordinates).pipe(take(1)).subscribe({
            next: (routeInfo) => {
                this.trackedRoute.next(routeInfo)
                this.trackedWaypoints.next(ride.waypoints)
            },
            error: (err) => {
                console.log(err)
            },
        });
    }
}