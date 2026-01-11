import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MENU_BY_ROLE, MenuItem } from './menu.config';
import { Role } from '../../../enums/role';

type SideMenuUser = {
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
  @Input() role: Role = Role.GUEST;
  @Input() user: SideMenuUser | null = null;

  @Input() avatarSrc?: string; // local browser path to avatar image that is created via URL.createObjectURL

  @Output() closed = new EventEmitter<void>();
  @Output() action = new EventEmitter<string>();

  get items(): MenuItem[] { return MENU_BY_ROLE[this.role] ?? []; }

  close() { this.closed.emit(); }
  onItem(a: string) { this.action.emit(a); }
}
