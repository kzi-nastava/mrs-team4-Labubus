import { Component, Input,  } from '@angular/core';

@Component({
  selector: 'app-stat-card',
  imports: [],
  templateUrl: './stat-card.html',
  styleUrl: './stat-card.css',
})
export class StatCard {
  @Input() value : string = "";
  @Input() label : string = "";
  @Input() variant : "light" | "dark" = "light";
  @Input() testId: string | null = null;
}
