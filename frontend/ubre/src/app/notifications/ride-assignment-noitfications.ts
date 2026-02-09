import { RideDto } from "../dtos/ride-dto";
import { NotificationType } from "../enums/notification-type";


export interface RideAssignmentNotification {
    status: NotificationType;
    ride: RideDto | null;
}

