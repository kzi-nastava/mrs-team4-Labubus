export class WaypointDto {
  public id : number;
  public label : string;
  public latitude : number;
  public longitude : number;

  constructor(id : number, label : string, latitude : number, longitude : number) {
      this.id = id;
      this.label = label;
      this.latitude = latitude;
      this.longitude = longitude;
  }
}