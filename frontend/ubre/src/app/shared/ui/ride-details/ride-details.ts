import { Component, Input } from '@angular/core';
import { ModalContainer } from '../modal-container/modal-container';
import { ProfileCard } from '../profile-card/profile-card';
import { VehicleCard } from '../vehicle-card/vehicle-card';
import { RouteTable } from '../route-table/route-table';
import { StatCard } from '../stat-card/stat-card';
import { Button } from '../button/button';
import { Ride } from '../../../models/ride';
import { DatePipe } from '@angular/common';
import { User } from '../../../models/user';

@Component({
  selector: 'app-ride-details',
  imports: [ProfileCard, VehicleCard, ModalContainer, RouteTable, DatePipe, StatCard, Button],
  templateUrl: './ride-details.html',
  styleUrl: './ride-details.css',
})
export class RideDetails {
  @Input({required: true}) ride! : Ride;
  @Input() user : User = {email: '', firstName: '', lastName: '', profilePicture: '', role: 'guest'}
}
