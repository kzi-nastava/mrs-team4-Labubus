import { NotificationType } from "../enums/notification-type";
import { RideDto } from "../dtos/ride-dto";

export interface CurrentRideNotification {
    status: NotificationType;
    ride: RideDto | null;
    reason: string | null;
}