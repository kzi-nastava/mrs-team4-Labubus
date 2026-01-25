import { VehicleIndicatorDto } from "../dtos/vehicle-indicator-dto";
import { NotificationType } from "../enums/notification-type";

export interface VehicleLocationNotification {
    status: NotificationType;
    indicators: VehicleIndicatorDto[] | null;
}