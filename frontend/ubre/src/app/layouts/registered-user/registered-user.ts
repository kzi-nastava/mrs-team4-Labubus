import { Component } from '@angular/core';
import { Map } from '../../features/map/map';
import { IconButton } from '../../shared/ui/icon-button/icon-button';
import { SideMenu } from '../../shared/ui/side-menu/side-menu';
import { Toast } from '../../shared/ui/toast/toast';
import { Modal } from '../../shared/ui/modal/modal';
import { ModalContainer } from '../../shared/ui/modal-container/modal-container';
import { StatCard } from '../../shared/ui/stat-card/stat-card';
import { Button } from '../../shared/ui/button/button';
import { ChangeDetectorRef } from '@angular/core';
import { Sheet } from '../../shared/ui/sheet/sheet';
import { FormsModule } from '@angular/forms';



type UserSettingsVM = {
  role: 'registered-user' | 'driver' | 'admin' ;
  avatarUrl: string;        
  email: string;
  passwordMasked: string;   
  name: string;
  surname: string;
  address: string;
  phone: string;
  activeLast24h?: string;   // driver
};



@Component({
  selector: 'app-registered-user',
  standalone: true,
  imports: [Map, IconButton, SideMenu, Toast, 
            Modal, ModalContainer, StatCard, 
            Button, Sheet, FormsModule],
  templateUrl: './registered-user.html',
  styleUrl: './registered-user.css',
})
export class RegisteredUser {

  constructor(private cdr: ChangeDetectorRef) {}

  user : UserSettingsVM = {
    role: 'driver',
    avatarUrl: 'default-avatar.jpg',
    email: 'john@doe.com',
    passwordMasked: '********',
    name: 'John',
    surname: 'Doe',
    address: '123 Main St, Anytown, USA',
    phone: '+1 234 567 8900',
    activeLast24h: '5h 30m',
  };

  editing : UserSettingsVM = { ...this.user };
  hidePassword = true;

  menuOpen = false;

  accountSettingsOpen = false;

  toastOpen = false;
  toastTitle = 'Ignore this toast';
  toastMessage = 'This is just a demo message for the toast';

  cdModalOpen = true; // Choose a destination modal

  checkoutModalOpen = true

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

  onCheckoutModalBack() {
    this.checkoutModalOpen = false
  }

  openChat() {
    // Open chat widget
  }

  openAccountSettings() { this.accountSettingsOpen = true; this.editing = { ...this.user }; }
  closeAccountSettings() { this.accountSettingsOpen = false; this.user = { ...this.editing }; }

  saveAccountSettings() {
    // Save account settings logic
    this.closeAccountSettings();
    this.showToast('Settings saved', 'Your account settings have been updated.');
  }

  onAccountSettingsBack() {
    this.closeAccountSettings();
    this.menuOpen = true;
  }

  onViewVehicleInfo() {
    this.showToast('Vehicle info', 'Clicked.');
  }

  onChangePassword() {
    // ovde kasnije otvaraš modal / sheet za promenu lozinke
    this.showToast('Change password', 'Clicked.');
  }
}

