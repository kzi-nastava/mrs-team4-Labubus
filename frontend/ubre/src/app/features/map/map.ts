import { Component, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})

export class Map implements AfterViewInit {
  private map!: L.Map;

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {

    const icon = L.icon({
      iconUrl: 'location.svg',
      iconSize:     [58, 58],
      iconAnchor:   [19, 38],
    });

    this.map = L.map('map', { 
      zoomControl : false, 
      attributionControl : false,
    }).setView([44.8200, 20.4481], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
    }).addTo(this.map);

    L.marker([44.8200, 20.4481], { icon }).addTo(this.map)
  }
}
