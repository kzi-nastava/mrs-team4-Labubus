export class ReviewDto {
    public id : number | null;
    public driverId : number | null;
    public userId : number | null;
    public rating : 1 | 2 | 3 | 4 | 5;
    public text : String;

    constructor(id : number, driverId : number, userId : number, rating : 1 | 2 | 3 | 4 | 5, text : String) {
        this.id = id;
        this.driverId = driverId;
        this.userId = userId;
        this.rating = rating;
        this.text = text;
    }
}