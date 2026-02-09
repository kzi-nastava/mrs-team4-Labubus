import { VehicleType } from "../enums/vehicle-type";

export class VehicleDto {
    public id : number;
    public model : string;
    public type : VehicleType;
    public plates : string;
    public seats : number;
    public babyFriendly : boolean;
    public petFriendly : boolean;

    constructor(id : number, model : string, type : VehicleType, plates : string, seats : number, babyFriendly : boolean, petFriendly : boolean) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.plates = plates;
        this.seats = seats;
        this.babyFriendly = babyFriendly;
        this.petFriendly = petFriendly;
    } 
}
