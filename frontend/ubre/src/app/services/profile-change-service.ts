import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ProfileChangeDto } from '../dtos/profile-change-dto';
import { ProfileChangeStatus } from '../enums/profile-change-status';

@Injectable({ providedIn: 'root' })
export class ProfileChangeService {
  private profileChanges : ProfileChangeDto[] = [
    {
      id: 1,
      userId: 2,
      oldName: 'Mika',
      newName: 'Mikael',
      oldSurname: 'Mikic',
      newSurname: 'Mikic',
      oldAddress: 'Test adress 123',
      newAddress: 'New adress 456',
      oldPhone: '1251323523',
      newPhone: '9876543210',
      oldAvatarUrl: 'default-avatar.jpg',
      newAvatarUrl: 'default-avatar.jpg',
      profileChangeStatus: ProfileChangeStatus.PENDING,
    }, 
    {
      id: 2,
      userId: 3,
      oldName: 'Ana',
      newName: 'Anamarija',
      oldSurname: 'Anic',
      newSurname: 'Anica',
      oldAddress: 'Old adress 789',
      newAddress: 'New address 101',
      oldPhone: '1234567890',
      newPhone: '4567890',
      oldAvatarUrl: 'default-avatar.jpg',
      newAvatarUrl: 'default-avatar.jpg',
      profileChangeStatus: ProfileChangeStatus.PENDING,
    },
    {
      id: 3,
      userId: 4,
      oldName: 'Jovan',
      newName: 'Jovan',
      oldSurname: 'Jovic',
      newSurname: 'Jovanovic',
      oldAddress: 'Some adress 111',
      newAddress: 'Another adress 222',
      oldPhone: '111222333',
      newPhone: '333222111',
      oldAvatarUrl: 'default-avatar.jpg',
      newAvatarUrl: 'default-avatar.jpg',
      profileChangeStatus: ProfileChangeStatus.PENDING,
    },
  ];

  getProfileChanges(): Observable<ProfileChangeDto[]> {
    return of(this.profileChanges);
  }

  accept(id: number): Observable<boolean> {
    // for now just remove from list (no status change)
    this.profileChanges = this.profileChanges.filter(pc => pc.id !== id);
    return of(true);
  }

  reject(id: number): Observable<boolean> {
    // for now just remove from list
    this.profileChanges = this.profileChanges.filter(pc => pc.id !== id);
    return of(true);
  }
}
