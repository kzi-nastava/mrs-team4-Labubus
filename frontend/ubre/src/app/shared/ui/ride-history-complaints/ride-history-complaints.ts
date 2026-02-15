import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ComplaintDto } from '../../../dtos/complaint-dto';
import { ComplaintService } from '../../../services/complaint-service';

@Component({
  selector: 'app-ride-history-complaints',
  imports: [],
  templateUrl: './ride-history-complaints.html',
  styleUrl: './ride-history-complaints.css',
})
export class RideHistoryComplaints implements OnInit, OnChanges{
  @Input() rideId!: number;

  complaints: ComplaintDto[] = [];

  constructor(private complaintService: ComplaintService) {}

  ngOnInit() {
    this.loadComplaints();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['rideId'] && !changes['rideId'].firstChange) {
      this.loadComplaints();
    }
  }

  loadComplaints() {

    this.complaintService.getComplaintsForRide(this.rideId).subscribe({
      next: (complaints) => {
        this.complaints = complaints;
      },
      error: (err) => {
        console.error('Failed to load complaints', err);
      },
    });
  }

}
