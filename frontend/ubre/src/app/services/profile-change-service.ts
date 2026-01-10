import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ProfileChangeDto } from '../dtos/profile-change-dto';
import { ProfileChangeStatus } from '../enums/profile-change-status';

@Injectable({ providedIn: 'root' })
export class ProfileChangeService {
  private profileChange: ProfileChangeDto = {
    id: 0,
    userId: 0,
    oldName: '',
    newName: '',
    oldSurname: '',
    newSurname: '',
    oldAddress: '',
    newAddress: '',
    oldPhone: '',
    newPhone: '',
    oldAvatarUrl: '',
    newAvatarUrl: '',
    profileChangeStatus: ProfileChangeStatus.PENDING,
  };

  getProfileChange(): Observable<ProfileChangeDto> {
    return of(this.profileChange);
  }
}
