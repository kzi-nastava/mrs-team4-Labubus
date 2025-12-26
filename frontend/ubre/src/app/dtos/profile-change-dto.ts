import { ProfileChangeStatus } from "../enums/profile-change-status";

export class ProfileChangeDto {
  public id : number;
  public userId : number;
  public oldName : string;
  public newName : string;
  public oldSurname : string;
  public newSurname : string;
  public oldAddress : string;
  public newAddress : string;
  public oldPhone : string;
  public newPhone : string;
  public oldAvatarUrl : string;
  public newAvatarUrl : string;

  public profileChangeStatus : ProfileChangeStatus;

  constructor(id : number, userId : number, oldName : string, newName : string, oldSurname : string, newSurname : string, oldAddress : string, newAddress : string, oldPhone : string, newPhone : string, oldAvatarUrl : string, newAvatarUrl : string, profileChangeStatus : ProfileChangeStatus) { 
      this.id = id;
      this.userId = userId;
      this.oldName = oldName;
      this.newName = newName;
      this.oldSurname = oldSurname;
      this.newSurname = newSurname;
      this.oldAddress = oldAddress;
      this.newAddress = newAddress;
      this.oldPhone = oldPhone;
      this.newPhone = newPhone;
      this.oldAvatarUrl = oldAvatarUrl;
      this.newAvatarUrl = newAvatarUrl;
      this.profileChangeStatus = profileChangeStatus;
  }
}

