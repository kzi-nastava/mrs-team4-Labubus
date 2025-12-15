import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [],
  templateUrl: './button.html',
  styleUrl: './button.css',
})
export class ButtonComponent {
  @Input() type: 'button' | 'submit' = 'button';
  @Input() disabled = false;
  @Input() variant: 'filled' | 'outlined' = 'filled';
  @Input() shadow = false;
}
