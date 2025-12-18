import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MENU_BY_ROLE, Role, MenuItem } from './menu.config';

type UserVM = {
  name: string;
  surname: string;
  phone?: string;
  avatarUrl?: string; // npr: 'avatars/me.png' iz public
};

@Component({
  selector: 'app-side-menu',
  standalone: true,
  templateUrl: './side-menu.html',
  styleUrl: './side-menu.css',
})
export class SideMenu {
  @Input() open = false;
  @Input() title = 'Account';
  @Input() role: Role = 'guest';
  @Input() user: UserVM | null = null;

  @Output() closed = new EventEmitter<void>();
  @Output() action = new EventEmitter<string>();

  get items(): MenuItem[] { return MENU_BY_ROLE[this.role] ?? []; }

  close() { this.closed.emit(); }
  onItem(a: string) { this.action.emit(a); }
}
