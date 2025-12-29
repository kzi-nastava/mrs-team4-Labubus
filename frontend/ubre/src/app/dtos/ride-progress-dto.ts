import { WaypointDto } from "./waypoint-dto";

export class RideProgressDto {
    public rideId : number;
    public location : WaypointDto;
    public estimatedTime : number;

    constructor(rideId : number, location : WaypointDto, estimatedTime : number) {
        this.rideId = rideId;
        this.location = location;
        this.estimatedTime = estimatedTime;
    }
}