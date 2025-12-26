import { UserDto } from "./user-dto";
import { VehicleDto } from "./vehicle-dto";
import { WaypointDto } from "./waypoint-dto";

export class RideDto {
    public id : number;
    public startTime : Date;
    public endTime : Date;
    public waypoints : WaypointDto[];
    public driver : UserDto;
    public vehicle : VehicleDto;
    public passengers : UserDto[];
    public price : number;
    public distance : number = 0;
    public panic : boolean = false;
    public canceledBy : number | null = null;

    constructor(id : number, startTime : Date, endTime : Date, waypoints : WaypointDto[], driver : UserDto, vehicle : VehicleDto, passengers : UserDto[], distance : number = 0, price : number, panic : boolean = false, canceledBy : number | null = null) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.waypoints = waypoints;
        this.driver = driver;
        this.vehicle = vehicle;
        this.passengers = passengers;
        this.price = price;
        this.distance = distance;
        this.panic = panic;
        this.canceledBy = canceledBy;
    }
}