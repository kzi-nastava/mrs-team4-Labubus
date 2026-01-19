import { Component, AfterViewInit, Output, EventEmitter, Input, OnChanges, SimpleChanges, NgZone } from '@angular/core';
import * as L from 'leaflet';
import { WaypointDto } from '../../../dtos/waypoint-dto';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit, OnChanges {
  private map!: L.Map;
  private markersLayer = L.layerGroup(); // layer group for the markers (waypoints)
  private routeLayer = L.geoJSON(); // layer group for the route (if available)
  private userMarker!: L.Marker;

  private userEmitted: boolean = false;
  
  private userIcon = L.icon({
    iconUrl: 'location.svg',
    iconSize: [62, 62],
    iconAnchor: [31, 62],
  });

  private waypointIcon = L.icon({
    iconUrl: 'waypoint.svg',
    iconSize: [62, 62],
    iconAnchor: [31, 62],
  });

  @Input() waypoints: WaypointDto[] = []; // waypoints to display on the map
  @Input() routeGeometry: GeoJSON.LineString | null = null; // route geometry to display on the map (if available)
  @Output() mapClick = new EventEmitter<{ lat: number; lon: number; }>();

  constructor(private zone: NgZone) {}

  ngAfterViewInit(): void {
    this.initMap();
    this.renderWaypoints();
    this.renderRoute();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['waypoints'] && this.map) {
      this.renderWaypoints();
    }

    if (changes['routeGeometry'] && this.map) {
      this.renderRoute();
    }
  }


  // MAP INITIALIZATION LOGIC
  private initMap(): void {
    this.map = L.map('map', { zoomControl: false, attributionControl: false });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(this.map);
    this.markersLayer.addTo(this.map);
    this.routeLayer.addTo(this.map);

    this.setCurrentLocation(); 

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const { lat, lng } = e.latlng;
      this.zone.run(() => this.mapClick.emit({ lat, lon: lng }));
    });
  }


  // RENDERING MARKERS AND ROUTE LOGIC
  private renderWaypoints(): void {
    this.markersLayer.clearLayers();

    for (const w of this.waypoints) {
      const m = L.marker([w.latitude, w.longitude], {
        icon: this.waypointIcon,
      }).addTo(this.markersLayer);

      m.bindPopup(w.label);
    }
  }

  private renderRoute(): void {
    console.log('renderRoute', this.routeGeometry);
    this.routeLayer.clearLayers();
    if (!this.routeGeometry) return;

    this.routeLayer.addData({ 
      type: 'Feature',
      properties: {},
      geometry: this.routeGeometry,
    } as any);

    this.map.fitBounds(this.routeLayer.getBounds(), { padding: [40, 40] });
  }


  // USER LOCATION LOGIC
  private setCurrentLocation(): void {
    if (!navigator.geolocation) return;
  
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const { latitude, longitude } = pos.coords;
  
        this.map.setView([latitude, longitude], 14);
  
        if (!this.userMarker) {
          this.userMarker = L.marker([latitude, longitude], {
            icon: this.userIcon,
          }).addTo(this.map);
        } else {
          this.userMarker.setLatLng([latitude, longitude]);
        }

        // very questionable, because who says that my starting location is the first waypoint? 
        // this is important for later, be careful.
        if (!this.userEmitted) {
          this.userEmitted = true;
          this.zone.run(() => this.mapClick.emit({ lat: latitude, lon: longitude }));
        }
      }
    );
  }
  
}
