import { UserDto } from "./user-dto";

export class ChatMessageDto {
    public id : number | null;
    public text : String;
    public sentAt : Date | null;
    public isRead : boolean | null;
    public chatId : number | null;
    public sender : UserDto | null;

    constructor(id : number | null, text : String, sentAt : Date | null, isRead : boolean | null, chatId : number | null, sender : UserDto | null) {
        this.id = id;
        this.text = text;
        this.sentAt = sentAt;
        this.isRead = isRead;
        this.chatId = chatId;
        this.sender = sender;
    }
}