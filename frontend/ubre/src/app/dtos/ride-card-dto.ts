import { WaypointDto } from "./waypoint-dto";

export class RideCardDto {
    public id : number;
    public startTime : Date;
    public waypoints : WaypointDto[];
  
    constructor(id : number, startTime : Date, waypoints : WaypointDto[]) {
        this.id = id;
        this.startTime = startTime;
        this.waypoints = waypoints;
    }
}