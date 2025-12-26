export class CancellationDto {
    public rideId : number;
    public reason : String;

    constructor(rideId : number, reason : String) {
        this.rideId = rideId;
        this.reason = reason;
    }
}