import { RideOptionsDto } from "../../dtos/ride-options-dto";
import { WaypointDto } from "../../dtos/waypoint-dto";

// NOTE: this is the response from the nominatim api, 
// main difference from WaypointDto is that it has id as string, 
// and lat and lon as string, and display_name is the label
export type NominatimItem = { 
  place_id: string;
  display_name: string;
  lat: string;
  lon: string;
};

export type RidePlanningState = {
  waypoints: WaypointDto[];
  query: string;
  suggestions: NominatimItem[];
  destOpen: boolean;
  routeInfo: RouteInfo | null;
  rideOptions: RideOptionsDto;
  scheduledTime: string; // will be in LocalDateTime format "yyyy-MM-dd'T'HH:mm:ss"
  passengerEmails: string[];
};

export type RouteInfo = { 
  distance: number; 
  duration: number; 
  geometry: GeoJSON.LineString; 
};

export type OsrmRouteResponse = {
    code: string; // "Ok" if successful, "NoRoute" if no route found, "InvalidQuery" if the query is invalid, "TooBig" if the request is too big
    routes: Array<RouteInfo>; // array of routes, each route has a distance, duration and geometry
};


