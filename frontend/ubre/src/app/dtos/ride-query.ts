export class RideQueryDto {
    public userId : number | null; // Odnosi se i na filtriranje po vozaču.
    public sortBy : String; // Ime atributa po kom se sortira.
    public ascending : boolean;
    public date : Date; // Datum po kom se filtrira.

    constructor(userId : number | null, sortBy : String, ascending : boolean, date : Date) {
        this.userId = userId;
        this.sortBy = sortBy;
        this.ascending = ascending;
        this.date = date;
    }
}