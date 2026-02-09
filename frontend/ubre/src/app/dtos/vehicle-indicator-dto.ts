import { UserStatus } from "../enums/user-status";
import { WaypointDto } from "./waypoint-dto";

export class VehicleIndicatorDto {
    public driverId : number;
    public location : WaypointDto;
    public status : UserStatus;
    public panic : boolean = false;

    constructor(driverId : number, location : WaypointDto, userStatus : UserStatus, panic : boolean = false) {
        this.driverId = driverId;
        this.location = location;
        this.status = userStatus;
        this.panic = panic;
    }
}