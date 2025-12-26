export class Vehicle {
    public model : string;
    public type : "Standard" | "Luxruy" | "Van";
    public image : string;

    constructor(model : string, type : "Standard" | "Luxruy" | "Van", image : string) {
        this.model = model;
        this.type = type;
        this.image = image;
    }
}