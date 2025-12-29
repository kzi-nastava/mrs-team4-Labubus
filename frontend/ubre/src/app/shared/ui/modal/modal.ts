import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Button } from '../button/button';

@Component({
  selector: 'app-modal',
  imports: [Button],
  standalone: true,
  templateUrl: './modal.html',
  styleUrl: './modal.css',
})
export class Modal {
  @Input() title = "";
  @Input() message = "";
  @Input() buttonText = "";

  @Output() action = new EventEmitter<void>();
}
