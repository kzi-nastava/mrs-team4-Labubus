export class WaypointDto {
  public id : number;
  public label : string;
  public latitude : number;
  public longitude : number;
  public visited : boolean;

  constructor(id : number, label : string, latitude : number, longitude : number, visited : boolean = false) {
      this.id = id;
      this.label = label;
      this.latitude = latitude;
      this.longitude = longitude;
      this.visited = visited;
  }
}