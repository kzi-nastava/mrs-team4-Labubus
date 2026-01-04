import { inject, Injectable } from '@angular/core';
import { UserDto } from '../dtos/user-dto';
import { Role } from '../enums/role';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private currentUser: UserDto = {
    email: 'mika@mikic.com',
    name: 'Mika',
    surname: 'Mikic',
    avatarUrl: '',
    role: Role.REGISTERED_USER,
    id: 2,
    phone: "1251323523",
    address: "Test adress 123"
  };

  private readonly http = inject(HttpClient);

  getCurrentUser() : Observable<UserDto> {
    return of(this.currentUser);
  }
}
