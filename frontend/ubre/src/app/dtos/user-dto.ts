import { Role } from "../enums/role";

export class UserDto {
    public id : number;
    public role : Role;
    public name : string;
    public surname : string;
    public email : string;
    public avatarUrl : string;
    public phone : string;
    public address : string;
    public isBlocked : boolean;

    constructor(id : number, role : Role, name : string, surname : string, email : string, avatarUrl : string, phone : string, address : string, isBlocked : boolean) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.phone = phone;
        this.address = address;
        this.isBlocked = isBlocked;
    }
}