import { WaypointDto } from "./waypoint-dto";

export class RideCardDto {
    public rideId : number;
    public startTime : Date;
    public waypoints : WaypointDto[];
  
    constructor(rideId : number, startTime : Date, waypoints : WaypointDto[]) {
        this.rideId = rideId;
        this.startTime = startTime;
        this.waypoints = waypoints;
    }
}