import { UserDto } from './user-dto';

export type ProfileChangeStatus = 'APPROVED' | 'REJECTED';

export interface ProfileChangeNotification {
  status: ProfileChangeStatus;
  user: UserDto | null;
}
