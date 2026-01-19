import { Component, AfterViewInit, Output, EventEmitter, Input, OnChanges, SimpleChanges, NgZone } from '@angular/core';
import * as L from 'leaflet';

type Waypoint = { id: string; label: string; lat: number; lon: number; };

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit, OnChanges {
  private map!: L.Map;
  private markersLayer = L.layerGroup();

  @Input() waypoints: Waypoint[] = [];
  @Output() mapClick = new EventEmitter<{ lat: number; lon: number; }>();

  private markerUrl = 'waypoint.svg';

  constructor(private zone: NgZone) {}

  ngAfterViewInit(): void {
    this.initMap();
    this.renderMarkers();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['waypoints'] && this.map) {
      this.renderMarkers();
    }
  }

  private initMap(): void {
    const icon = L.icon({
      iconUrl: 'location.svg',
      iconSize: [62, 62],
      iconAnchor: [31, 62],
    });

    this.map = L.map('map', {
      zoomControl: false,
      attributionControl: false,
    }).setView([45.2671, 19.8335], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
    }).addTo(this.map);

    L.marker([45.2671, 19.8335], { icon }).addTo(this.map);

    this.markersLayer.addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const { lat, lng } = e.latlng;
      this.zone.run(() => {
        this.mapClick.emit({ lat, lon: lng });
      });
    });
  }

  private renderMarkers(): void {
    this.markersLayer.clearLayers();

    const markerIcon = this.markerUrl
      ? L.icon({
          iconUrl: this.markerUrl,
          iconSize: [62, 62],
          iconAnchor: [31, 62],
        })
      : undefined;

    for (const w of this.waypoints) {
      const m = L.marker([w.lat, w.lon], markerIcon ? { icon: markerIcon } : undefined)
        .addTo(this.markersLayer);

      m.bindPopup(w.label);
    }
  }
}
