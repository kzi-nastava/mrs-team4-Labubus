import { WaypointDto } from "./waypoint-dto";

export class RideCardDto {
    public rideId : number;
    public startTime : Date;
    public waypoints : WaypointDto[];
    public favorite : boolean;
  
    constructor(rideId : number, startTime : Date, waypoints : WaypointDto[], favorite : boolean) {
        this.rideId = rideId;
        this.startTime = startTime;
        this.waypoints = waypoints;
        this.favorite = favorite;
    }
}