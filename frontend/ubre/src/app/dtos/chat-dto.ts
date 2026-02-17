import { UserDto } from "./user-dto";

export class ChatDto {
    public id : number | null;
    public user : UserDto | null;
    public hasUnreadMessages : boolean | null;

    constructor(id : number | null, user : UserDto | null, hasUnreadMessage : boolean | null) {
        this.id = id;
        this.user = user;
        this.hasUnreadMessages = hasUnreadMessage;
    }
}