import { DriverStatus } from "../enums/user-status";
import { WaypointDto } from "./waypoint-dto";

export class VehicleIndicatorDto {
    public driverId : number;
    public location : WaypointDto;
    public driverStatus : DriverStatus;
    public panic : boolean = false;

    constructor(driverId : number, location : WaypointDto, driverStatus : DriverStatus, panic : boolean = false) {
        this.driverId = driverId;
        this.location = location;
        this.driverStatus = driverStatus;
        this.panic = panic;
    }
}