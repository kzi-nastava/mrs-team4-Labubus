import { Component, AfterViewInit, Output, EventEmitter, Input, OnChanges, SimpleChanges, NgZone } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet.marker.slideto';
import 'leaflet-rotatedmarker';
import { WaypointDto } from '../../../dtos/waypoint-dto';
import { VehicleIndicatorDto } from '../../../dtos/vehicle-indicator-dto';
import { RideDto } from '../../../dtos/ride-dto';
import { RouteInfo } from '../ride-types';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit, OnChanges {
  private map!: L.Map;
  private markersLayer = L.layerGroup(); // layer group for the markers (waypoints)
  private vehicleLayer = L.layerGroup(); // layer for vehicle indicators
  private routeLayer = L.geoJSON(); // layer group for the route (if available)
  private rideWaypointsLayer = L.layerGroup(); // layer group for currently tracked ride waypoints
  private rideRouteLayer = L.geoJSON(); // layer group for currently tracked ride route
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
    iconSize: [Math.max(this.vehicleIndicatorWidth * Math.pow(2, -5), 17), Math.max(this.vehicleIndicatorHeight * Math.pow(2, -5), 30)],
    iconAnchor: [Math.max(this.vehicleIndicatorWidth * Math.pow(2, -5), 17) / 2, Math.max(this.vehicleIndicatorHeight * Math.pow(2, -5), 30) / 2],
  });

  private vehiclePanicIcon = L.icon({
    iconUrl: 'car_panic.svg',
    iconSize: [Math.max(this.vehicleIndicatorWidth * Math.pow(2, -3), 30), Math.max(this.vehicleIndicatorHeight * Math.pow(2, -3), 50)],
    iconAnchor: [Math.max(this.vehicleIndicatorWidth * Math.pow(2, -3), 30) / 2, Math.max(this.vehicleIndicatorHeight * Math.pow(2, -3), 50) / 2],
  });

  @Input() waypoints: WaypointDto[] = []; // waypoints to display on the map
  @Input() vehicles : VehicleIndicatorDto[] = [];
  @Input() routeGeometry: GeoJSON.LineString | null = null; // route geometry to display on the map (if available)
  @Input() displayedRideRoute: RouteInfo | null = null;
  @Input() displayedRideWaypoints: WaypointDto[] = [];
  @Output() mapClick = new EventEmitter<{ lat: number; lon: number; }>();

  constructor(private zone: NgZone) {}

  ngAfterViewInit(): void {
    this.initMap();
    this.renderWaypoints();
    this.renderVehicles();
    this.renderRoute();

    this.map.on('zoomend', () => {
      // This scales the cars on zoom, but they end up being almost invisible on larger zooms
      const newVehicleHeght = Math.max(this.vehicleIndicatorHeight * Math.pow(2, this.map.getZoom() - 19), 30)
      const newVehiclWidth = Math.max(this.vehicleIndicatorWidth * Math.pow(2, this.map.getZoom() - 19), 17)

      this.vehicleIcon = L.icon({
        iconUrl: 'car.svg',
        iconSize: [newVehiclWidth, newVehicleHeght],
        iconAnchor: [newVehiclWidth / 2, newVehicleHeght / 2],
      })

      const panicHeight = Math.max(this.vehicleIndicatorHeight * 1.5 * Math.pow(2, this.map.getZoom() - 19), 60);
      const panicWidth  = Math.max(this.vehicleIndicatorWidth  * 1.5 * Math.pow(2, this.map.getZoom() - 19), 40);

      this.vehiclePanicIcon = L.icon({
        iconUrl: 'car_panic.svg',
        iconSize: [panicWidth, panicHeight],
        iconAnchor: [panicWidth / 2, panicHeight / 2],
      });
    
      this.vehicleLayer.getLayers().forEach((layer : L.Layer) => {
        if (layer instanceof L.Marker) {
          const vehicle : VehicleIndicatorDto | undefined = this.vehicles.find(v => v.driverId == (layer as any).driverId)
          if (vehicle && vehicle.panic)
            layer.setIcon(this.vehiclePanicIcon)
          else
            layer.setIcon(this.vehicleIcon);
        }
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

    if (changes['displayedRideRoute'] && this.map) {
      this.renderRideRoute()
    }

    if (changes['displayedRideWaypoints'] && this.map) {
      this.renderRideWaypoints()
    }
  }

  // MAP INITIALIZATION LOGIC
  private initMap(): void {
    this.map = L.map('map', { zoomControl: false, attributionControl: false });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(this.map);
    this.markersLayer.addTo(this.map);
    this.routeLayer.addTo(this.map);
    this.vehicleLayer.addTo(this.map);
    this.rideRouteLayer.addTo(this.map)
    this.rideWaypointsLayer.addTo(this.map)
    
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
    let existingMarkers : L.Marker[] = this.vehicleLayer.getLayers().filter(layer => layer instanceof L.Marker);
    for (const v of this.vehicles) {
      let found : boolean = false;

      for(const marker of existingMarkers) {
        if ((marker as any).driverId == v.driverId) {
          if (marker.getLatLng().lat != v.location.latitude || marker.getLatLng().lng != v.location.longitude) {
            const bearing = this.calculateBearing(marker.getLatLng().lat, marker.getLatLng().lng, v.location.latitude, v.location.longitude);
            (marker as any).setRotationOrigin('center center');
            (marker as any).setRotationAngle(bearing);
          }

          if ((v as any).mapCenter) {
            this.followMarker(marker.getLatLng(), new L.LatLng(v.location.latitude, v.location.longitude), 1000);
            if (this.displayedRideRoute != null)
              marker.bindTooltip(`<b>~ ${Math.ceil(this.displayedRideRoute.duration / 60)} min left</b>`, {
                permanent: true,
                direction: 'left',
                className: 'tracking-estimation'
              }).openTooltip();
          }
          else
            marker.unbindTooltip();
          (marker as any).slideTo([v.location.latitude, v.location.longitude], {duration: 1000, keepAtCenter: (v as any).mapCenter})
          marker.setPopupContent(v.location.label + ` (${v.status})`)
          marker.setIcon(v.panic ? this.vehiclePanicIcon : this.vehicleIcon)
          found = true;
          existingMarkers = existingMarkers.filter(marker => (marker as any).driverId != v.driverId)
          break;
        }
      }

      if (!found) {
        const m = L.marker([v.location.latitude, v.location.longitude], {
          icon: v.panic ? this.vehiclePanicIcon : this.vehicleIcon,
        }).addTo(this.vehicleLayer);
        (m as any).driverId = v.driverId;
  
        m.bindPopup(v.location.label + ` (${v.status})`);

        if ((v as any).mapCenter) {
            this.map.setView([v.location.latitude, v.location.longitude])
            if (this.displayedRideRoute != null)
              m.bindTooltip(`<b>~ ${Math.ceil(this.displayedRideRoute.duration / 60)} min left</b>`, {
                permanent: true,
                direction: 'left',
                className: 'tracking-estimation'
              }).openTooltip();
          }
      }
    }

    existingMarkers.forEach((marker : L.Marker) => {
      this.vehicleLayer.removeLayer(marker);
    })
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

  private renderRideRoute() {
    this.rideRouteLayer.clearLayers();
    if (!this.displayedRideRoute) return;

    this.rideRouteLayer.addData({ 
      type: 'Feature',
      properties: {},
      geometry: this.displayedRideRoute.geometry,
    } as any);

    // this.map.fitBounds(this.rideRouteLayer.getBounds(), { padding: [40, 40] });
  }

  private renderRideWaypoints() {
    this.rideWaypointsLayer.clearLayers();

    if (this.displayedRideWaypoints.length < 1) {
      this.map.dragging.enable()
      this.map.keyboard.enable()
    }
    else {
      this.map.dragging.disable()
      this.map.keyboard.disable()
      this.map.setZoom(19);
    }

    for (const w of this.displayedRideWaypoints) {
      const m = L.marker([w.latitude, w.longitude], {
        icon: this.waypointIcon,
      }).addTo(this.rideWaypointsLayer);

      m.bindPopup(w.label);
    }
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

  // Helper functions for calculations and animations
  private toRad(degree: number): number {
    return degree * Math.PI / 180;
  }

  private toDeg(radian: number): number {
    return radian * 180 / Math.PI;
  }

  private calculateBearing(startLat: number, startLng: number, endLat: number, endLng: number): number {
    const start = L.latLng(startLat, startLng);
    const end = L.latLng(endLat, endLng);

    const startLngRad = this.toRad(start.lng);
    const startLatRad = this.toRad(start.lat);
    const endLngRad = this.toRad(end.lng);
    const endLatRad = this.toRad(end.lat);

    const deltaLng = endLngRad - startLngRad;
    const y = Math.sin(deltaLng) * Math.cos(endLatRad);
    const x = Math.cos(startLatRad) * Math.sin(endLatRad) -
              Math.sin(startLatRad) * Math.cos(endLatRad) * Math.cos(deltaLng);
    const bearing = this.toDeg(Math.atan2(y, x));

    // Normalize to 0-360 degrees
    return (bearing + 360) % 360;
  }

  private followMarker(
    from: L.LatLng,
    to: L.LatLng,
    durationMs: number
  ): void {
    const start = performance.now();

    const animate = (now: number) => {
      const elapsed = now - start;
      const t = Math.min(elapsed / durationMs, 1);

      const eased = t < 0.5
        ? 2 * t * t
        : 1 - Math.pow(-2 * t + 2, 2) / 2;

      const lat = from.lat + (to.lat - from.lat) * eased;
      const lng = from.lng + (to.lng - from.lng) * eased;

      this.map.panTo([lat, lng], { animate: false });

      if (t < 1) {
        requestAnimationFrame(animate);
      }
    };
  }
}
