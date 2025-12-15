import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-icon-button',
  standalone: true,
  imports: [],
  templateUrl: './icon-button.html',
  styleUrl: './icon-button.css',
})
export class IconButton {
  @Input() ariaLabel: string = 'Icon Button';
  @Input() badge: boolean = false;
  @Input() iconUrl: string | null = null;
}
