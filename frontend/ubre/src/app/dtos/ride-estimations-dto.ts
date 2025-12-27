import { WaypointDto } from "./waypoint-dto";

export class RideEstimationsDto {
    public waypoints : WaypointDto[];
    public price : number;
    public duration : number;

    constructor(waypoints : WaypointDto[], price : number, duration : number) {
        this.waypoints = waypoints;
        this.price = price;
        this.duration = duration;
    }
}