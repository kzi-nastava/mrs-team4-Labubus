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
};
