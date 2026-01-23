import { WaypointDto } from "./waypoint-dto";

export class RideCardDto {
    public id : number;
    public startTime : Date;
    public waypoints : WaypointDto[];
    public favorite : boolean;
  
    constructor(id : number, startTime : Date, waypoints : WaypointDto[], favorite : boolean) {
        this.id = id;
        this.startTime = startTime;
        this.waypoints = waypoints;
        this.favorite = favorite;
    }
}