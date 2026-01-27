import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ComplaintService } from '../../../services/complaint-service';
import { ComplaintDto } from '../../../dtos/complaint-dto';
import { HttpErrorResponse } from '@angular/common/http';
import { ModalContainer } from '../modal-container/modal-container';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-complaint-modal',
  imports: [ModalContainer, AsyncPipe],
  templateUrl: './complaint-modal.html',
  styleUrl: './complaint-modal.css',
})
export class ComplaintModal {
  @Input() show : Observable<boolean> = of(false);
  @Output() onError = new EventEmitter<Error>();

  private complaintService : ComplaintService = inject(ComplaintService)

  complaint : ComplaintDto = {
    id: null,
    driverId: null,
    userId: null,
    text: ""
  }
  error : boolean = false;

  onClose(event : Event) {
    if (event.target === event.currentTarget) {
      this.complaintService.cancelComplaint();
    }
  }

  onSetText(event : Event) {
    this.complaint.text = (event.target as HTMLInputElement).value
    if (this.complaint.text != "")
      this.error = false; 
  }

  onSubmit() {
    if (this.complaint.text == "") {
      this.error = true;
      return;
    }

    this.complaintService.submitComplaint(this.complaint, {
        next: (value : ComplaintDto) => {
          this.complaintService.cancelComplaint();
        },
        error: (err : HttpErrorResponse) => {
          this.onError.emit(new Error(err.error))
        }
      })
  }
}
