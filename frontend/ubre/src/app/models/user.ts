export class User {
    public email : string;
    public firstName : string;
    public lastName : string;
    public profilePicture : string;
    public role : 'registered-user' | 'driver' | 'admin' | 'guest';

    constructor(email : string, firstName : string, lastName : string, profilePicture : string, role :  'registered-user' | 'driver' | 'admin' | 'guest') {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.role = role;
    }
}