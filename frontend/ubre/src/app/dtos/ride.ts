import { User } from "./user"
import { Vehicle } from "./vehicle"

export class Ride {
    public id : Number;
    public startTime : Date;
    public endTime : Date;
    public waypoints : string[];
    public driver : User;
    public vehicle : Vehicle;
    public passengers : User[];
    public price : Number;
    public travelDistance : Number = 0;
    public panicActivated : boolean = false;
    public canceledBy : string | null = null;

    constructor(id : Number, startTime : Date, endTime : Date, waypoints : string[], driver : User, vehicle : Vehicle, passengers : User[], travelDistance : Number = 0, price : Number, panicActivated : boolean = false, canceledBy : string | null = null) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.waypoints = waypoints;
        this.driver = driver;
        this.vehicle = vehicle;
        this.passengers = passengers;
        this.price = price;
        this.travelDistance = travelDistance;
        this.panicActivated = panicActivated;
        this.canceledBy = canceledBy;
    }
}