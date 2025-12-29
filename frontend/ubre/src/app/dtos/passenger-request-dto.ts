export class PassengerRequestDto {
  public rideId : number;
  public passengerEmail : string;

  constructor(rideId : number, passengerEmail : string) {
      this.rideId = rideId;
      this.passengerEmail = passengerEmail;
  }
}