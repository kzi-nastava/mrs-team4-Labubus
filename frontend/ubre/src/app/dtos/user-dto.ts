import { Role } from "../shared/ui/side-menu/menu.config";

export class UserDto {
    public id : number;
    public role : Role;
    public name : string;
    public surname : string;
    public email : string;
    public avatarUrl : string;
    public phone : string;
    public address : string;

    constructor(id : number, role : Role, name : string, surname : string, email : string, avatarUrl : string, phone : string, address : string) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.phone = phone;
        this.address = address;
    }
}