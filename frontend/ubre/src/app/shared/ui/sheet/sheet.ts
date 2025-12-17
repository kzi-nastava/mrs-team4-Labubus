import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-sheet',
  imports: [],
  standalone: true,
  templateUrl: './sheet.html',
  styleUrl: './sheet.css',
})
export class Sheet {
  @Input() open = false;
  @Input( { required: true }) title!: string;

  @Output() closed = new EventEmitter<void>();
  @Output() back = new EventEmitter<void>();

  close() { this.closed.emit(); }
  onBack() { this.back.emit(); }
}
