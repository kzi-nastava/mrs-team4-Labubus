import { NotificationType } from '../enums/notification-type';
import { UserDto } from '../dtos/user-dto';

export interface ProfileChangeNotification {
  status: NotificationType;
  user: UserDto | null;
}
