import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BlockUsersService } from '../../../services/block-users-service';
import { UserDto } from '../../../dtos/user-dto';
import { Role } from '../../../enums/role';

@Component({
  selector: 'app-block-users-list',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './block-users-list.html',
  styleUrl: './block-users-list.css',
})
export class BlockUsersList implements OnInit {
  users: UserDto[] = [];
  loading = true;
  error: string | null = null;
  Role = Role;

  /** Korisnik čija se kartica raširila za unos napomene pre blokiranja */
  expandingForBlock: UserDto | null = null;
  blockNote = '';

  constructor(private blockUsersService: BlockUsersService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.error = null;
    this.expandingForBlock = null;
    this.blockNote = '';
    this.blockUsersService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load users.';
        this.loading = false;
      },
    });
  }

  openBlockNote(user: UserDto): void {
    this.expandingForBlock = user;
    this.blockNote = '';
  }

  cancelBlockNote(): void {
    this.expandingForBlock = null;
    this.blockNote = '';
  }

  confirmBlock(user: UserDto): void {
    const note = this.blockNote.trim() || undefined;
    this.blockUsersService.blockUser(user.id, note).subscribe({
      next: () => {
        user.isBlocked = true;
        this.expandingForBlock = null;
        this.blockNote = '';
      },
      error: () => {
        this.error = 'Failed to block user.';
      },
    });
  }

  unblock(user: UserDto): void {
    this.blockUsersService.unblockUser(user.id).subscribe({
      next: () => {
        user.isBlocked = false;
      },
      error: () => {
        this.error = 'Failed to unblock user.';
      },
    });
  }

  roleLabel(role: Role): string {
    switch (role) {
      case Role.ADMIN: return 'Admin';
      case Role.DRIVER: return 'Driver';
      case Role.REGISTERED_USER: return 'Passenger';
      default: return role;
    }
  }
}
