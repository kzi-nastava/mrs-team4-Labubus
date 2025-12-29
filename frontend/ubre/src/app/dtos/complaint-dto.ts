export class ComplaintDto {
    public id : number;
    public driverId : number;
    public userId : number;
    public text : String;

    constructor(id : number, driverId : number, userId : number, text : String) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.text = text;
    }
}