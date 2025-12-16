import { Component } from '@angular/core';
import { Map } from '../../features/map/map';
import { IconButton } from '../../shared/ui/icon-button/icon-button';
import { SideMenu } from '../../shared/ui/side-menu/side-menu';
import { Toast } from '../../shared/ui/toast/toast';
import { Modal } from '../../shared/ui/modal/modal';

@Component({
  selector: 'app-registered-user',
  standalone: true,
  imports: [Map, IconButton, SideMenu, Toast, Modal],
  templateUrl: './registered-user.html',
  styleUrl: './registered-user.css',
})
export class RegisteredUser {
  showMenuButton = true;
  menuOpen = false;

  toastOpen = false;
  toastTitle = 'Ignore this toast';
  toastMessage = 'This is just a demo message for the toast';

  cdModalOpen = true; // Choose a destination modal

  user = {
    name: 'John Doe',
    phone: '+44 7700 900123',
  };

  openMenu() { this.menuOpen = true; }
  closeMenu() { this.menuOpen = false; }
  openCdModal() { this.cdModalOpen = true; }
  closeCdModal() { this.cdModalOpen = false; }

  handleMenuAction(a: string) {
    if (a === 'logout') { /* logout */ }
    this.closeMenu();
  }

  showToast(title: string, message: string) {
    this.toastTitle = title;
    this.toastMessage = message;
    this.toastOpen = true;
  }

  hideToast() {
    this.toastOpen = false;
  }

  onCdModalAction() {
    this.cdModalOpen = false;
    this.showToast('Destination chosen', 'You have successfully chosen a destination.');
  }
}

