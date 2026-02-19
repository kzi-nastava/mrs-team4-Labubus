import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-panic-button',
  imports: [],
  templateUrl: './panic-button.html',
  styleUrl: './panic-button.css',
})
export class PanicButton {
  @Input() disabled: boolean = false;
  @Output() panicClick = new EventEmitter<void>();
  onClick() {
    if (!this.disabled) {
      this.panicClick.emit();
    }
  }
}
