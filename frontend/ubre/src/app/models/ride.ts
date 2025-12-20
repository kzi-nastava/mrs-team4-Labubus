export class Ride {
    public id : Number;
    public time : Date;
    public waypoints : string[];

    constructor(id : Number, time : Date, waypoints : string[]) {
        this.id = id;
        this.time = time;
        this.waypoints = waypoints;
    }
}