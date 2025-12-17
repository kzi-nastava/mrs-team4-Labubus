import { Component } from '@angular/core';
import { Map } from '../../features/map/map';
import { IconButton } from '../../shared/ui/icon-button/icon-button';
import { SideMenu } from '../../shared/ui/side-menu/side-menu';
import { Toast } from '../../shared/ui/toast/toast';
import { Modal } from '../../shared/ui/modal/modal';
import { ChangeDetectorRef } from '@angular/core';
import { Button } from '../../shared/ui/button/button';
import { Sheet } from '../../shared/ui/sheet/sheet';

@Component({
  selector: 'app-registered-user',
  standalone: true,
  imports: [Map, IconButton, SideMenu, Toast, Modal, Button, Sheet],
  templateUrl: './registered-user.html',
  styleUrl: './registered-user.css',
})
export class RegisteredUser {

  constructor(private cdr: ChangeDetectorRef) {}

  user = {
    name: 'John Doe',
    phone: '+44 7700 900123',
  };

  menuOpen = false;

  accountSettingsOpen = false;

  toastOpen = false;
  toastTitle = 'Ignore this toast';
  toastMessage = 'This is just a demo message for the toast';

  cdModalOpen = true; // Choose a destination modal

  openMenu() { this.menuOpen = true; }
  closeMenu() { this.menuOpen = false; }
  openCdModal() { this.cdModalOpen = true; }
  closeCdModal() { this.cdModalOpen = false; }

  closeAllSidePanels() {
    this.closeMenu();
    this.closeAccountSettings();
  }

  handleMenuAction(action: string) {
    if (action === 'logout') { /* logout */ }
    if (action === 'account-settings') { this.openAccountSettings(); }
    this.closeMenu();
  }

  showToast(title: string, message: string) {
    this.toastTitle = title;
    this.toastMessage = message;
    this.toastOpen = true;

    setTimeout(() => {
      this.hideToast();
      this.cdr.detectChanges();
    }, 3000);
  }

  hideToast() { this.toastOpen = false; }

  onCdModalAction() {
    this.cdModalOpen = false;
    this.showToast('Destination chosen', 'You have successfully chosen a destination.');
  }

  openChat() {
    // Open chat widget
  }

  openAccountSettings() { this.accountSettingsOpen = true; }
  closeAccountSettings() { this.accountSettingsOpen = false; }

  saveAccountSettings() {
    // Save account settings logic
    this.closeAccountSettings();
    this.showToast('Settings saved', 'Your account settings have been updated.');
  }

  onAccountSettingsBack() {
    this.closeAccountSettings();
    this.menuOpen = true;
  }

}

