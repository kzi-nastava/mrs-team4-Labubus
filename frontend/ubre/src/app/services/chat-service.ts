import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, take } from 'rxjs';
import { ChatDto } from '../dtos/chat-dto';
import { ChatMessageDto } from '../dtos/chat-message-dto';
import { HttpClient } from '@angular/common/http';
import { UserService } from './user-service';
import { WebSocketService } from './websocket-service';
import { UserDto } from '../dtos/user-dto';
import { Role } from '../enums/role';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private readonly BASE_URL : string = "http://localhost:8080/api/";
  private readonly http = inject(HttpClient);
  private readonly userService = inject(UserService)
  private readonly websocketService = inject(WebSocketService)

  constructor() {
    this.userService.currentUser$.subscribe((user : UserDto) => {
      if (user.role == Role.GUEST) {
        this.chats.next([])
        this.currentChat.next(null)
        this.currentChatMessages.next(null)
        this.unreadCount.next(0)
      }
      else {
        if (user.role == Role.ADMIN) {
           this.fetchChats();

          this.websocketService.adminChatMessageNotifications().subscribe((message : ChatMessageDto) => {
            message.sentAt = new Date((message as any).sentAt)
            this.fetchChats()
            this.addChatMessage(message)
            this.playNotificationSound()
          })
        }
        else {
          this.fetchMyChat();

          this.websocketService.chatMessageNotifications(user.id).subscribe((message : ChatMessageDto) => {
            message.sentAt = new Date((message as any).sentAt)
            if (this.currentChat.value == null) {
              this.http.get<ChatDto>(`${this.BASE_URL}chats/my`).pipe(take(1)).subscribe((currentChat : ChatDto) => {
                this.currentChat.next(currentChat);
                if (currentChat == null)
                  this.currentChatMessages.next([message])
                else
                  this.addChatMessage(message)
              })
            }
            else
              this.addChatMessage(message)
            this.playNotificationSound()
          })
        }

        this.http.get<number>(`${this.BASE_URL}chats/unread`).pipe(take(1)).subscribe((count : number) => {
         this.unreadCount.next(count);
        })
      }
    })
  }

  private readonly chats : BehaviorSubject<ChatDto[]> = new BehaviorSubject<ChatDto[]>([])
  public readonly chats$ : Observable<ChatDto[]> = this.chats.asObservable();

  private readonly currentChat : BehaviorSubject<ChatDto | null> = new BehaviorSubject<ChatDto | null>(null)
  public readonly currentChat$ : Observable<ChatDto | null> = this.currentChat.asObservable();

  private readonly currentChatMessages : BehaviorSubject<ChatMessageDto[] | null> = new BehaviorSubject<ChatMessageDto[] | null>(null)
  public readonly currentChatMessages$ : Observable<ChatMessageDto[] | null> = this.currentChatMessages.asObservable();

  private readonly unreadCount : BehaviorSubject<number> = new BehaviorSubject<number>(0)
  public readonly unreadCount$ : Observable<number> = this.unreadCount.asObservable();

  public getChats() : ChatDto[] {
    return this.chats.value;
  }

  public setChats(chats : ChatDto[]) : void {
    this.chats.next(chats)
  }

  public getCurrentChat() : ChatDto | null {
    return this.currentChat.value;
  }

  public setCurrentChat(chat : ChatDto | null) : void {
    this.currentChat.next(chat)
  }

  public getCurrentChatMessages() : ChatMessageDto[] | null {
    return this.currentChatMessages.value;
  }

  public setCurrentChatMessages(messages : ChatMessageDto[] | null) : void {
    this.currentChatMessages.next(messages)
  }

  public getUnreadCount() : number {
    return this.unreadCount.value;
  }

  public setUnreadCount(value : number) : void {
    this.unreadCount.next(value)
  }

  public addChatMessage(message : ChatMessageDto) {
    if (this.currentChatMessages.value != null && this.currentChat.value != null && this.currentChat.value.id == message.chatId) {
      const newMessages : ChatMessageDto[] = [message, ...this.currentChatMessages.value]
      this.currentChatMessages.next(newMessages);
      this.userService.getCurrentUser().pipe(take(1)).subscribe(user => {
        if (user.role != message.sender?.role) {
          this.http.put<void>(`${this.BASE_URL}chats/messages/${message.id}/mark`, {}).pipe(take(1)).subscribe({
            next: () => {},
            error: (err) => {console.log(err)}
          })
        }
      })
    }
    else {
      this.unreadCount.next(this.unreadCount.value + 1)
    }
  }

  private fetchChats() {
    this.http.get<ChatDto[]>(`${this.BASE_URL}chats`).pipe(take(1)).subscribe((adminChats : ChatDto[]) => {
     this.chats.next(adminChats.sort((chat1, chat2) => Number(chat2.hasUnreadMessages) - Number(chat1.hasUnreadMessages)));
    })
  }

  private fetchMyChat() {
    this.http.get<ChatDto>(`${this.BASE_URL}chats/my`).pipe(take(1)).subscribe((currentChat : ChatDto) => {
      this.currentChat.next(currentChat);
      if (currentChat == null)
        this.currentChatMessages.next([])
    })
  }

  public fetchChatMessages() {
    this.http.get<ChatMessageDto[]>(`${this.BASE_URL}chats/${this.getCurrentChat()?.id}/messages`).pipe(take(1)).subscribe((messages : ChatMessageDto[]) => {
      this.userService.getCurrentUser().pipe(take(1)).subscribe(user => {
        for (let message of messages) {
          message.sentAt = new Date((message as any).sentAt)
          if (!message.isRead && message.sender?.role != user.role) {
            message.isRead = true
            this.unreadCount.next(this.unreadCount.value - 1)
            console.log(this.unreadCount.value)
            this.http.put<void>(`${this.BASE_URL}chats/messages/${message.id}/mark`, {}).pipe(take(1)).subscribe({
              next: () => {},
              error: (err) => {console.log(err)}
            })
          }
        }
        this.currentChatMessages.next(messages || []);
        this.syncCurrentChatWithChats();
      })
    })
  }

  public sendMessage(message : ChatMessageDto) {
    this.http.post<void>(`${this.BASE_URL}chats`, message).pipe(take(1)).subscribe({
      next: () => {

      },
      error: (err) => {
        console.log(err)
      }
    })
  }

  public syncCurrentChatWithChats() {
    if (this.chats.value != null) {
      const tempCopy = this.chats.value
      for (const chat of tempCopy) {
        if (chat.id == this.currentChat.value?.id) {
          chat.hasUnreadMessages = false;
          break
        }
      }
      this.chats.next(tempCopy);
    }
  }

  playNotificationSound() {
    try {
      const audio = new Audio('/new-notification-07-210334.mp3');
      
      audio.volume = 0.3;
      
      audio.play().catch((error) => {
        console.warn('Failed to play notification sound:', error);
        const audioAlt = new Audio('new-notification-07-210334.mp3');
        audioAlt.volume = 0.3;
        audioAlt.play().catch((err) => {
          console.warn('Failed to play notification sound with alternative path:', err);
        });
      });
    } catch (error) {
      console.warn('Error creating audio element:', error);
    }
  }
}
