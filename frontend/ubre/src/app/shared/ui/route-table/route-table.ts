import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-route-table',
  imports: [],
  templateUrl: './route-table.html',
  styleUrl: './route-table.css',
})
export class RouteTable {
  @Input({required: true}) waypoints : string[] = [];
}
