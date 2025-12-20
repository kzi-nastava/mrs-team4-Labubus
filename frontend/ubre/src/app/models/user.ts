export class User {
    public email : string;
    public firstName : string;
    public lastName : string;
    public profilePicture : string;
    public role : "admin" | "driver" | "user";

    constructor(email : string, firstName : string, lastName : string, profilePicture : string, role :  "admin" | "driver" | "user") {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.role = role;
    }
}