import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RideCardDto } from '../../../dtos/ride-card-dto';

@Component({
  selector: 'app-ride-card',
  imports: [DatePipe],
  templateUrl: './ride-card.html',
  styleUrl: './ride-card.css',
})
export class RideCard {
  @Input() ride : RideCardDto | null = null;
  @Input() iconUrl : string = "http://localhost:4200/favorite_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg"
  @Input() selected : boolean = false;
  @Output() select = new EventEmitter<any>();
  @Output() action = new EventEmitter<any>();

  onSelect(event : MouseEvent) {
    const clickedElement = event.target as Element;
    if (!clickedElement.matches('.action-icon'))
      this.select.emit(this.ride)
  }

  onAction() {
    this.action.emit(this.ride)
  }
}
