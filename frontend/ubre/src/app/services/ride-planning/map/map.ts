import { Component, AfterViewInit, Output, EventEmitter, Input, OnChanges, SimpleChanges, NgZone } from '@angular/core';
import * as L from 'leaflet';
import { WaypointDto } from '../../../dtos/waypoint-dto';
import { VehicleIndicatorDto } from '../../../dtos/vehicle-indicator-dto';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit, OnChanges {
  private map!: L.Map;
  private markersLayer = L.layerGroup(); // layer group for the markers (waypoints)
  private vehicleLayer = L.layerGroup();
  private routeLayer = L.geoJSON(); // layer group for the route (if available)
  private userMarker!: L.Marker;

  private userEmitted: boolean = false;
  private readonly vehicleIndicatorHeight = 81
  private readonly vehicleIndicatorWidth = 46
  
  private userIcon = L.icon({
    iconUrl: 'location.svg',
    iconSize: [62, 62],
    iconAnchor: [31, 56],
  });

  private waypointIcon = L.icon({
    iconUrl: 'waypoint.svg',
    iconSize: [62, 62],
    iconAnchor: [31, 56],
  });

  private vehicleIcon = L.icon({
    iconUrl: 'car.svg',
    iconSize: [this.vehicleIndicatorWidth, this.vehicleIndicatorHeight],
    iconAnchor: [this.vehicleIndicatorWidth / 2, this.vehicleIndicatorHeight / 2],
  });

  @Input() waypoints: WaypointDto[] = []; // waypoints to display on the map
  @Input() vehicles : VehicleIndicatorDto[] = [];
  @Input() routeGeometry: GeoJSON.LineString | null = null; // route geometry to display on the map (if available)
  @Output() mapClick = new EventEmitter<{ lat: number; lon: number; }>();

  constructor(private zone: NgZone) {}

  ngAfterViewInit(): void {
    this.initMap();
    this.renderWaypoints();
    this.renderVehicles();
    this.renderRoute();

    this.map.on('zoomend', () => {
      // This scales the cars on zoom, but they end up being almost invisible on larger zooms
      const newVehicleHeght = this.vehicleIndicatorHeight * Math.pow(2, this.map.getZoom() - 19)
      const newVehiclWidth = this.vehicleIndicatorWidth * Math.pow(2, this.map.getZoom() - 19)
      console.log(newVehiclWidth, newVehicleHeght, this.map.getZoom(), Math.pow(2, this.map.getZoom() - 19))
      this.vehicleIcon = L.icon({
        iconUrl: 'car.svg',
        iconSize: [newVehiclWidth, newVehicleHeght],
        iconAnchor: [newVehiclWidth / 2, newVehicleHeght / 2],
      })
      this.vehicleLayer.getLayers().forEach((layer : L.Layer) => {
        if (layer instanceof L.Marker)
        layer.setIcon(this.vehicleIcon);
      })
    })
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['waypoints'] && this.map) {
      this.renderWaypoints();
    }

    if (changes['vehicles'] && this.map) {
      this.renderVehicles();
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
    this.vehicleLayer.addTo(this.map);
    
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

  private renderVehicles(): void {
    this.vehicleLayer.clearLayers();

    for (const v of this.vehicles) {
      const m = L.marker([v.location.latitude, v.location.longitude], {
        icon: this.vehicleIcon,
      }).addTo(this.vehicleLayer);

      m.bindPopup(v.location.label);
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
    // Added basic fallback location if the geolocation permissions are denied since the target region is know to be Novi Sad
    const fallback: [number, number] = [45.264180, 19.830198];

    this.map.setView(fallback, 14);

    this.userMarker = L.marker(fallback, {
      icon: this.userIcon,
    }).addTo(this.map);

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
      }
    );
  }
}
