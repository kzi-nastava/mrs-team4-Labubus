import { Component, Input, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [],
  templateUrl: './button.html',
  styleUrl: './button.css',
})
export class Button {
  @Input() type: 'button' | 'submit' = 'button';
  @Input() disabled: boolean = false;
  @Input() variant: 'filled' | 'outlined' = 'filled';
  @Input() shadow: boolean = false;
  @Input() width: string | null = null;
  @Input() testId: string | null = null;

  @Output() clicked = new EventEmitter<void>();
}
