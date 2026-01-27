export class ComplaintDto {
    public id : number | null;
    public driverId : number | null;
    public userId : number | null;
    public text : String;

    constructor(id : number, driverId : number, userId : number, text : String) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.text = text;
    }
}