import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal-container',
  imports: [],
  templateUrl: './modal-container.html',
  styleUrl: './modal-container.css',
})
export class ModalContainer {
  @Input() showTitleBar : boolean = true
  @Input() title : string = "Title"
  @Output() backPressed  = new EventEmitter<void>();
  @Output() onScrollContent  = new EventEmitter<Event>();

  back() {
    this.backPressed.emit();
  }

  scroll(event : Event) {
    this.onScrollContent.emit(event)
  }
}
