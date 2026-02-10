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
    public trackedWaypoints : BehaviorSubject<WaypointDto[]> = new BehaviorSubject<WaypointDto[]>([]);
    public readonly trackedWaypoints$ = this.trackedWaypoints.asObservable();

    constructor() {
        this.ridePlanningStore.currentRide$.subscribe((ride : RideDto | null) => {
            console.log(ride, "Rerender?")
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
        let coordinates : WaypointDto[] = [];
        for (const i in ride.waypoints)
            if (!ride.waypoints[i].visited)
                coordinates.push(ride.waypoints[i]);
        if (currentLocation != undefined)
            coordinates = [currentLocation, ...coordinates]

        this.routingService.route(coordinates).pipe(take(1)).subscribe({
            next: (routeInfo) => {
                if (coordinates.length < 2)
                    this.trackedRoute.next({
                        distance: 0,
                        duration: 0,
                        geometry: {
                            type: 'LineString',
                            coordinates: []
                        }
                    });
                else
                    this.trackedRoute.next(routeInfo)
                if (this.trackedWaypoints.value.length != coordinates.length - 1)
                    this.trackedWaypoints.next(coordinates.slice(1))
            },
            error: (err) => {
                console.log(err)
                if (coordinates.length < 2)
                    this.trackedRoute.next({
                        distance: 0,
                        duration: 0,
                        geometry: {
                            type: 'LineString',
                            coordinates: []
                        }
                    });
            },
        });
    }
}