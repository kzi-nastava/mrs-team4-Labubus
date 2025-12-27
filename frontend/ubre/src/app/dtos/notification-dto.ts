import { NotificationType } from "../enums/notification-type";

export class NotificationDto {
    public id : number;
    public userId : number;
    public title : String;
    public message : String;
    public read : boolean;
    public type : NotificationType;

    constructor(id : number, userId : number, title : String, message : String, read : boolean, type : NotificationType) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.read = read;
        this.type = type;
    }
}
