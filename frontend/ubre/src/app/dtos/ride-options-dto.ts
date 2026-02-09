import { VehicleType } from "../enums/vehicle-type";

export class RideOptionsDto {
    public vehicleType : VehicleType;
    public babyFriendly : boolean;
    public petFriendly : boolean;

    constructor(vehicleType : VehicleType, babyFriendly : boolean, petFriendly : boolean) {
        this.vehicleType = vehicleType;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    }
}