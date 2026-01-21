import { RideStatus } from "../enums/ride-status";
import { UserDto } from "./user-dto";
import { VehicleDto } from "./vehicle-dto";
import { WaypointDto } from "./waypoint-dto";

export class RideDto {
    public id : number;
    public startTime : string;
    public endTime : string;
    public waypoints : WaypointDto[];
    public driver : UserDto;
    public vehicle : VehicleDto;
    public passengers : UserDto[];
    public price : number;
    public distance : number = 0;
    public panic : boolean = false;
    public canceledBy : number | null = null;
    public status : RideStatus;
    public createdBy : number;

    constructor(id : number, startTime : string, endTime : string, waypoints : WaypointDto[], driver : UserDto, vehicle : VehicleDto, passengers : UserDto[], distance : number = 0, price : number, panic : boolean = false, canceledBy : number | null = null, status : RideStatus, createdBy : number) {
        this.id = id;
        this.start = startTime;
        this.end = endTime;
        this.waypoints = waypoints;
        this.driver = driver;
        this.vehicle = vehicle;
        this.passengers = passengers;
        this.price = price;
        this.distance = distance;
        this.panic = panic;
        this.canceledBy = canceledBy;
        this.status = status;
        this.createdBy = createdBy;
    }
}