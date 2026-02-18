import { Component, Input } from '@angular/core';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-icon-button',
  standalone: true,
  imports: [NgClass],
  templateUrl: './icon-button.html',
  styleUrl: './icon-button.css',
})
export class IconButton {
  @Input() ariaLabel = 'Icon Button';
  @Input() badge = false;
  @Input() iconUrl: string | null = null;
  @Input() type: 'red' | 'yellow' | null = null;
  @Input() disabled: boolean = false;
  @Input() testId: string | null = null;
}
