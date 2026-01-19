import { WaypointDto } from "../../dtos/waypoint-dto";

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
