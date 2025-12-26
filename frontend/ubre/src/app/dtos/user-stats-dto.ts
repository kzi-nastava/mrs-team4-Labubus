export class UserStatsDto {
    public activePast24Hours : number;
    public numberOfRides : number;
    public distanceTravelled : number; 
    public moneySpent : number;
    public moneyEarned : number;

    constructor(activePast24Hours : number, numberOfRides : number, distanceTravelled : number, moneySpent : number, moneyEarned : number) {
        this.activePast24Hours = activePast24Hours;
        this.numberOfRides = numberOfRides;
        this.distanceTravelled = distanceTravelled;
        this.moneySpent = moneySpent;
        this.moneyEarned = moneyEarned;
    }
}