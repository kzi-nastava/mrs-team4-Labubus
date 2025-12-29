export class LoginDto {
    public email : String;
    public passwordHash : String;

    constructor(email : String, passwordHash : String) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}