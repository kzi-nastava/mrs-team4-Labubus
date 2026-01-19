/*
    PURPOSE OF THIS STORE IS TO HANDLE THE RIDE PLANNING PROCESS, 
    I.E. ADDING AND REMOVING WAYPOINTS, SETTING THE QUERY AND SUGGESTIONS, 
    SETTING THE DESTINATION OPEN, SETTING THE ROUTE, AND RESETING THE STORE.

    NO HTTP REQUESTS IN THE STORE AND LEAFLET.
*/

import { inject, Injectable } from "@angular/core";
import { GeocodingService } from "./geocoding-service";
import { BehaviorSubject } from "rxjs";
import { RidePlanningState, NominatimItem } from "./ride-types";
import { WaypointDto } from "../../dtos/waypoint-dto";
import { take } from "rxjs";

@Injectable({ providedIn: 'root' })
export class RidePlanningStore {
    private geocodingService = inject(GeocodingService);
    
    private ridePlanningStateSubject$ = new BehaviorSubject<RidePlanningState>({ // this represents from now on the state of the ride planning process
        waypoints: [],  // types come implicitly from the RidePlanningState type
        query: '',
        suggestions: [],
        destOpen: false,
    });


    SUGGESTION_DEBOUNCE_TIME = 400; 
    QUERY_MIN_LENGTH = 3;


    readonly ridePlanningState$ = this.ridePlanningStateSubject$.asObservable();
    private suggestionTimer: any = null; // this is a timer for the suggestions, it is used to debounce the suggestions, it is used to prevent the suggestions from being fetched too often
    private suggestionRequestId = 0; // this is a request id for the suggestions, it is used to prevent the suggestions from being fetched too often

    get waypoints() { return this.ridePlanningStateSubject$.value.waypoints; }
    get query() { return this.ridePlanningStateSubject$.value.query; }
    get suggestions() { return this.ridePlanningStateSubject$.value.suggestions; }
    get destOpen() { return this.ridePlanningStateSubject$.value.destOpen; }

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
            id: Number(suggestion.place_id),
            label: suggestion.display_name,
            latitude: Number(suggestion.lat),
            longitude: Number(suggestion.lon),
        };
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: [...this.waypoints, wp] });
        this.clearSuggestions();
        this.setQuery('');
    }

    addFromMapClick(lat: number, lon: number) {
        if (!this.destOpen) return;
        const id = crypto.randomUUID();
        const fallback = `${lat.toFixed(5)}, ${lon.toFixed(5)}`; // fallback is a fallback for the label, if the label is not found

        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: [...this.waypoints, { id: Number(id), label: fallback, latitude: lat, longitude: lon }] });
        
        this.geocodingService.reverse(lat, lon).pipe(take(1)).subscribe({
            next: (label) => {
                if (label) {
                    this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: this.waypoints.map((w) => (w.id === Number(id) ? { ...w, label: label ?? fallback } : w)) });
                }
            },
            error: () => {},
        });
    }

    removeWaypoint(id: number) {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: this.waypoints.filter((w) => w.id !== id) });
    }

    resetDest() {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, waypoints: [], suggestions: [], query: '', destOpen: false });
    }

    private clearSuggestions() {
        this.ridePlanningStateSubject$.next({ ...this.ridePlanningStateSubject$.value, suggestions: [] });
    }




}

