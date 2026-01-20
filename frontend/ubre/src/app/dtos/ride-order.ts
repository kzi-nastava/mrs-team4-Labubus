import { VehicleType } from "../enums/vehicle-type";
import { WaypointDto } from "./waypoint-dto";

// this dto is used to create a ride order, its sent to backend to create a new ride
export class RideOrderDto {
    public id : number;
    public creatorId : number;
    public passengerEmails : string[];
    public waypoints : WaypointDto[];
    public vehicleType : VehicleType;
    public babyFriendly : boolean;
    public petFriendly : boolean;
    public scheduledTime : string; // null if not scheduled
    public distance : number; // the distance of the ride in meters
    public requiredTime : number; // the required time of the ride in seconds
    public price : number; // the price of the ride in euros

    constructor(id : number, creatorId : number, passengerEmails : string[], waypoints : WaypointDto[], vehicleType : VehicleType, babyFriendly : boolean, petFriendly : boolean, scheduledTime : string, distance : number, requiredTime : number, price : number) {
        this.id = id;
        this.creatorId = creatorId;
        this.passengerEmails = passengerEmails;
        this.waypoints = waypoints;
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
        this.scheduledTime = scheduledTime;
        this.distance = distance;
        this.requiredTime = requiredTime;
        this.price = price;
    }
}




    