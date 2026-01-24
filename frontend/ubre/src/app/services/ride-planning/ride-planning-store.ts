/*
    PURPOSE OF THIS STORE IS TO HANDLE THE RIDE PLANNING PROCESS, 
    I.E. ADDING AND REMOVING WAYPOINTS, SETTING THE QUERY AND SUGGESTIONS, 
    SETTING THE DESTINATION OPEN, SETTING THE ROUTE, AND RESETING THE STORE.

    NO HTTP REQUESTS IN THE STORE AND LEAFLET.
*/

import { inject, Injectable } from "@angular/core";
import { GeocodingService } from "./geocoding-service";
import { BehaviorSubject, catchError, Observable, tap, throwError } from "rxjs";
import { RidePlanningState, NominatimItem } from "./ride-types";
import { WaypointDto } from "../../dtos/waypoint-dto";
import { take } from "rxjs";
import { RoutingService } from "./routing-service";
import { VehicleType } from "../../enums/vehicle-type";
import { RideOptionsDto } from "../../dtos/ride-options-dto";
import { OrderingService } from "./ordering-service";
import { RideOrderDto } from "../../dtos/ride-order";
import { UserService } from "../user-service";
import { RideDto } from "../../dtos/ride-dto";
import { HttpErrorResponse } from "@angular/common/http";
import { RideStatus } from "../../enums/ride-status";

@Injectable({ providedIn: 'root' })
export class RidePlanningStore {
    private geocodingService = inject(GeocodingService);
    private routingService = inject(RoutingService);
    private orderingService = inject(OrderingService);
    private userService = inject(UserService);
    
    private ridePlanningStateSubject$ = new BehaviorSubject<RidePlanningState>({ // this represents from now on the state of the ride planning process
        waypoints: [],  // types come implicitly from the RidePlanningState type
        query: '',
        suggestions: [],
        destOpen: false,
        routeInfo: null, // null if no route info is available
        rideOptions: {
            vehicleType: VehicleType.STANDARD,
            babyFriendly: false,
            petFriendly: false,
        },
        scheduledTime: '',
        passengersEmails: [],
        price: null,
    });
    readonly ridePlanningState$ = this.ridePlanningStateSubject$.asObservable();

    public clearRidePlanningState() {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: [], suggestions: [], query: '', destOpen: false, routeInfo: null, rideOptions: { vehicleType: VehicleType.STANDARD, babyFriendly: false, petFriendly: false }, scheduledTime: '', passengersEmails: [], price: null });
    }

    public clearAllExceptRouteAndWaypoints() {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, suggestions: [], query: '', destOpen: false, rideOptions: { vehicleType: VehicleType.STANDARD, babyFriendly: false, petFriendly: false }, scheduledTime: '', passengersEmails: [], price: null });
    }


    // there should be also be a subject for ongoing, or schedulded rides. TODO: implement this


    SUGGESTION_DEBOUNCE_TIME = 400; 
    QUERY_MIN_LENGTH = 3;



    private suggestionTimer: any = null; // this is a timer for the suggestions, it is used to debounce the suggestions, it is used to prevent the suggestions from being fetched too often
    private suggestionRequestId = 0; // this is a request id for the suggestions, it is used to prevent the suggestions from being fetched too often
    private routingRequestId = 0;  // for preventing old routing requests from being executed
    
    
    
    get waypoints() { return this.ridePlanningStateSubject$.value.waypoints; }
    get query() { return this.ridePlanningStateSubject$.value.query; }
    get suggestions() { return this.ridePlanningStateSubject$.value.suggestions; }
    get destOpen() { return this.ridePlanningStateSubject$.value.destOpen; }
    get routeInfo() { return this.ridePlanningStateSubject$.value.routeInfo; }
    get price() { return this.ridePlanningStateSubject$.value.price; }
    get scheduledTime() { return this.ridePlanningStateSubject$.value.scheduledTime; }
    get passengersEmails() { return this.ridePlanningStateSubject$.value.passengersEmails; }
    get rideOptions() { return this.ridePlanningStateSubject$.value.rideOptions; }

    openDest() { this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, destOpen: true }); }
    closeDest() { this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, destOpen: false }); }

    toggleDestOpen() { 
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, destOpen: !this.ridePlanningStateSubject$.value.destOpen });
        if (!this.ridePlanningStateSubject$.value.destOpen) this.clearSuggestions();
    }

    setQuery(query: string) {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, query: query });
        this.onQueryChange();
    }

    // on query change, we need to clear the suggestions and fetch new suggestions  
    private onQueryChange() {
        const q = this.query.trim();
        if (this.suggestionTimer) clearTimeout(this.suggestionTimer);
        if (q.length < this.QUERY_MIN_LENGTH) {
            this.clearSuggestions(); return;
        }
        this.suggestionTimer = setTimeout(() => {
            const requestId = ++this.suggestionRequestId;
            this.geocodingService.search(q).subscribe((items) => {
                if (requestId !== this.suggestionRequestId) return;
                this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, suggestions: items });
            });
        }, this.SUGGESTION_DEBOUNCE_TIME); 
    }


    // ADD FROM SUGGESTIONS LOGIC

    addFromSuggestion(suggestion: NominatimItem) {
        const wp: WaypointDto = {
            id: Number(Date.now()), // this is a unique id for the waypoint (we could use the place_id, but it is not consistent - its OSRM database id) 
            label: this.geocodingService.toLatin(suggestion.display_name),
            latitude: Number(suggestion.lat),
            longitude: Number(suggestion.lon),
        };
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: [...this.waypoints, wp] });
        this.clearSuggestions();
        this.setQuery('');

        this.recalculateRoute(); // recalculate the route after adding a waypoint from a suggestion, maybe new route is optimal
    }

    addFromMapClick(lat: number, lon: number) {
        if (!this.destOpen) return;
        const id = Date.now();
        const fallback = `${lat.toFixed(6)}, ${lon.toFixed(6)}`; // fallback is a fallback for the label, if the label is not found

        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: [...this.waypoints, { id: Number(id), label: fallback, latitude: lat, longitude: lon }] });
        
        this.geocodingService.reverse(lat, lon).pipe(take(1)).subscribe({
            next: (label) => {
                if (label) {
                    this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: this.waypoints.map((w) => (w.id === Number(id) ? { ...w, label: label ?? fallback } : w)) });
                }
            },
            error: () => {},
        });

        this.recalculateRoute();
    }

    removeWaypoint(id: number) {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: this.waypoints.filter((w) => w.id !== id) });
        this.recalculateRoute();
    }

    addCurrentLocationAsFirstWaypoint() {
        if (!navigator.geolocation) {
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (pos) => {
                const { latitude, longitude } = pos.coords;
                const id = Date.now();
                const fallback = `${latitude.toFixed(5)}, ${longitude.toFixed(5)}`;

                // Add as first waypoint
                const newWaypoint: WaypointDto = {
                    id: Number(id),
                    label: fallback,
                    latitude: latitude,
                    longitude: longitude,
                };

                const currentWaypoints = this.waypoints;
                this.ridePlanningStateSubject$.next({
                    ...this.ridePlanningStateSubject$.value,
                    waypoints: [newWaypoint, ...currentWaypoints]
                });

                // Try to get reverse geocoded label
                this.geocodingService.reverse(latitude, longitude).pipe(take(1)).subscribe({
                    next: (label) => {
                        if (label) {
                            const currentState = this.ridePlanningStateSubject$.value;
                            this.ridePlanningStateSubject$.next({
                                ...currentState,
                                waypoints: currentState.waypoints.map((w) => (w.id === Number(id) ? { ...w, label: label ?? fallback } : w))
                            });
                        }
                    },
                    error: () => {},
                });

                this.recalculateRoute();
            },
            (error) => {
                console.error('Error getting current location:', error);
            }
        );
    }

    resetDest() {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: [], suggestions: [], query: '', destOpen: false, routeInfo: null, rideOptions: { vehicleType: VehicleType.STANDARD, babyFriendly: false, petFriendly: false }, scheduledTime: '', passengersEmails: [], price: null });
    }

    setRideOptions(options: RideOptionsDto) {
        // Reset price when ride options change, as price depends on vehicle type
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, rideOptions: options, price: null });
    }

    setWaypoints(waypoints: WaypointDto[]) {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: waypoints });
    }

    setPrice(price: number) {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, price: price });
    }

    setScheduledTime(timeData: { hours: number; minutes: number; isAM: boolean }) {
        // now we are receiving time data in 12 hour format, we need to convert it to 24 hour format for the backend
        let hours = timeData.hours;

        if (!timeData.isAM && hours < 12) hours += 12;
        if (timeData.isAM && hours === 12) hours = 0;

        const now = new Date();
        const pad = (num: number) => num.toString().padStart(2, '0');

        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, scheduledTime: `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(hours)}:${pad(timeData.minutes)}:00` });
    }

    clearScheduledTime() {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, scheduledTime: '' });
    }

    setPassengersEmails(emails: string[]) {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, passengersEmails: emails });
    }

    private clearSuggestions() {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, suggestions: [] });
    }


    // RECALCULATE ROUTE LOGIC
    public recalculateRoute() {
        const wps = this.waypoints;
        if (wps.length < 2) {
            this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, routeInfo: null });
            return;
        }

        const requestId = ++this.routingRequestId;

        this.routingService.route(wps).pipe(take(1)).subscribe({
            next: (routeInfo) => {
                if (requestId !== this.routingRequestId) return;
                this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, routeInfo: routeInfo });
            },
            error: () => {
                if (requestId !== this.routingRequestId) return;
                this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, routeInfo: null });
            },
        });
    }



    // PRICE ESTIMATION LOGIC
    public estimatePrice() {
        this.orderingService.estimatePrice(this.routeInfo?.distance ?? 0, this.rideOptions.vehicleType).pipe(take(1)).subscribe({
            next: (price) => {
                this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, price: price });
            },
            error: () => {
                this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, price: null });
            },
        });
    }





    // ============ RIDE ORDERING LOGIC ============

    // for a driver, it represents the ride that is currently ongoing.
    // for a user, it represents a ride that is currently being driven, or scheduled for the future.
    // this is hard to name because it is initially empty, when receiven its pending until it is accepted by a driver.
    // TODO: find a better name for this subject.

    public currentRideSubject$ = new BehaviorSubject<RideDto | null>(null);  // this subject represents current ride, for user and for a driver. 
    public currentRide$ = this.currentRideSubject$.asObservable();

    public getCurrentRide() {
        return this.currentRideSubject$.value;
    }
    public clearCurrentRide() {
        this.currentRideSubject$.next(null);
    }

    public setCurrentRide(ride: RideDto) {
        this.currentRideSubject$.next(ride);
    }

    // ORDER RIDE LOGIC
    public orderRide(): Observable<RideDto> {
        const rideOrderDto: RideOrderDto = {
            id: 0,
            creatorId: this.userService.getCurrentUserId(),
            passengersEmails: this.passengersEmails,
            waypoints: this.waypoints,
            vehicleType: this.rideOptions.vehicleType,
            babyFriendly: this.rideOptions.babyFriendly,
            petFriendly: this.rideOptions.petFriendly,
            scheduledTime: this.scheduledTime,
            distance: this.routeInfo?.distance ?? 0,
            requiredTime: this.routeInfo?.duration ?? 0,
            price: this.price ?? 0,
        };

        return this.orderingService.orderRide(rideOrderDto).pipe(
            take(1),
            tap((ride: RideDto) => {
                this.currentRideSubject$.next(ride);
                this.clearRidePlanningState();
            }),
            catchError((err: HttpErrorResponse) => {
                this.clearAllExceptRouteAndWaypoints();
                return throwError(() => err);
            })
        );
    }

    // when ride is started, request is sent to backend to change status from PENDING to IN_PROGRESS
    public startCurrentRide(): Observable<void> {
        return this.orderingService.startCurrentRide(this.currentRideSubject$.value?.id ?? 0).pipe(
            take(1),
            tap(() => {
                this.currentRideSubject$.next({ ...this.currentRideSubject$.value!, status: RideStatus.IN_PROGRESS });
            }),
            catchError((err: HttpErrorResponse) => {
                return throwError(() => err);
            })
        );
    }
}