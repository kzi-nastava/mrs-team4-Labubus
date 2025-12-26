import { VehicleDto } from "./vehicle-dto";

export class DriverRegistrationDto {
  public id : number;
  public avatarUrl : string;
  public email : string;
  public password : string;
  public name : string;
  public surname : string;
  public phone : string;
  public address : string;

  public vehicle : VehicleDto;

  constructor(id : number, avatarUrl : string, email : string, password : string, name : string, surname : string, phone : string, address : string, vehicle : VehicleDto) {
      this.id = id;
      this.avatarUrl = avatarUrl;
      this.email = email;
      this.password = password;
      this.name = name;
      this.surname = surname;
      this.phone = phone;
      this.address = address;
      this.vehicle = vehicle;
  }
}