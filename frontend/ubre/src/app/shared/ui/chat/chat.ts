import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { ModalContainer } from '../modal-container/modal-container';
import { ChatService } from '../../../services/chat-service';
import { AsyncPipe, DatePipe } from '@angular/common';
import { UserService } from '../../../services/user-service';
import { ProfileCard } from '../profile-card/profile-card';
import { FormsModule } from '@angular/forms';
import { ChatMessageDto } from '../../../dtos/chat-message-dto';
import { take } from 'rxjs';
import { ChatDto } from '../../../dtos/chat-dto';
import { Role } from '../../../enums/role';

@Component({
  selector: 'app-chat',
  imports: [ModalContainer, ProfileCard, AsyncPipe, FormsModule, DatePipe],
  templateUrl: './chat.html',
  styleUrl: './chat.css',
})
export class Chat {
  @Input() open : boolean = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onError = new EventEmitter<Error>();

  messageText : String = ""
  error : boolean = false

  chatService : ChatService = inject(ChatService);
  userService : UserService = inject(UserService);

  onBack() {
    this.userService.getCurrentUser().pipe(take(1)).subscribe(user => {
      if (this.chatService.getCurrentChat() != null && user.role == Role.ADMIN) {
        this.chatService.setCurrentChat(null)
        this.chatService.setCurrentChatMessages(null)
      }
      else
        this.onClose.emit()
    })
  }

  onSelectChat(chat : ChatDto) {
    this.chatService.setCurrentChat(chat);
    console.log(chat)
    this.chatService.fetchChatMessages();
  }

  onSend() {
    if (this.messageText.trim() == "") {
      this.error = true
      return
    }

    const chatId : number | null | undefined = this.chatService.getCurrentChat()?.id;
    this.userService.getCurrentUser().pipe(take(1)).subscribe(user => {
      const newMessage : ChatMessageDto = new ChatMessageDto(null, this.messageText.trim(), new Date(), false, chatId || null, user);
      this.chatService.sendMessage(newMessage);
      this.messageText = ""
    })
  }
}
