import { WaypointDto } from "../../dtos/waypoint-dto";

export type NominatimItem = {
  id: number;
  label: string;
  latitude: string;
  longitude: number;
};

export type RidePlanningState = {
  waypoints: WaypointDto[];
  query: string;
  suggestions: NominatimItem[];
  destOpen: boolean;
};
