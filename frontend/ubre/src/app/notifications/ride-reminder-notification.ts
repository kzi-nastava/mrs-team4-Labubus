import { NotificationType } from "../enums/notification-type";

export interface RideReminderNotification {
    status: NotificationType;
    time: string; // in format "HH:MM"
}