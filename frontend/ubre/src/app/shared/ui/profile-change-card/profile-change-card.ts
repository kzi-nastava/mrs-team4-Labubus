import { Component, inject } from '@angular/core';
import { Input, Output, EventEmitter } from '@angular/core';
import { ProfileChangeDto } from '../../../dtos/profile-change-dto';
import { BehaviorSubject, Observable, take } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-profile-change-card',
  imports: [AsyncPipe],
  templateUrl: './profile-change-card.html',
  styleUrl: './profile-change-card.css',
  standalone : true,
})
export class ProfileChangeCard {
  @Input({ required: true }) item!: ProfileChangeDto;
  @Output() approve = new EventEmitter<number>();
  @Output() reject = new EventEmitter<number>();

  private readonly http = inject(HttpClient);
  private readonly api = 'http://localhost:8080/api';

  leaving = false;

  onApprove() { this.leaveThen(() => this.approve.emit(this.item.id)); }
  onReject() { this.leaveThen(() => this.reject.emit(this.item.id)); }

  private leaveThen(fn: () => void) {
    if (this.leaving) return;
    this.leaving = true;
    setTimeout(fn, 280);
  }


  // new aavatar url that is fetched from the server

  private avatarSrcSubject = new BehaviorSubject<string>('default-avatar.jpg');
  readonly avatarSrc$ = this.avatarSrcSubject.asObservable();

  ngOnInit() {
    this.loadAvatar(this.item.userId);
  }
  ngOnDestroy() {
    this.avatarSrcSubject.complete();
  }

  getUserAvatar(userId: number): Observable<Blob> {
    return this.http.get(`${this.api}/users/${userId}/avatar`, { responseType: 'blob' });
  }
  
  loadAvatar(userId: number) {
    this.getUserAvatar(userId).pipe(take(1)).subscribe({
      next: blob => this.avatarSrcSubject.next(URL.createObjectURL(blob)),
      error: () => this.avatarSrcSubject.next('default-avatar.jpg'),
    });
  }

}
