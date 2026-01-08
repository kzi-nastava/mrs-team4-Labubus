import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

export type Waypoint = { id: string; label: string; lat: number; lon: number };
export type NominatimItem = { place_id: string; display_name: string; lat: string; lon: string };

type State = {
  waypoints: Waypoint[];
  query: string;
  suggestions: NominatimItem[];
  destOpen: boolean;
};

@Injectable({ providedIn: 'root' })
export class MapService {
  private http = inject(HttpClient);

  private state$ = new BehaviorSubject<State>({
    waypoints: [],
    query: '',
    suggestions: [],
    destOpen: false,
  });

  // selectors (read-only)
  readonly vm$ = this.state$.asObservable();

  // simple sync getters (opciono)
  get waypoints() { return this.state$.value.waypoints; }
  get suggestions() { return this.state$.value.suggestions; }
  get query() { return this.state$.value.query; }
  get destOpen() { return this.state$.value.destOpen; }
  get currentRoute() { return this.waypoints.map(w => [w.lat, w.lon] as [number, number]); }

  private suggestTimer: any = null;
  private suggestReqId = 0;

  openDest() { this.patch({ destOpen: true }); }
  closeDest() { this.patch({ destOpen: false }); }

  toggleDest() {
    this.patch({ destOpen: !this.destOpen });
    if (!this.destOpen) this.clearSuggestions();
  }

  setQuery(v: string) {
    this.patch({ query: v });
    this.onQueryChange();
  }

  private onQueryChange() {
    const q = this.query.trim();

    if (this.suggestTimer) clearTimeout(this.suggestTimer);

    if (q.length < 3) {
      this.clearSuggestions();
      return;
    }

    this.suggestTimer = setTimeout(() => {
      const reqId = ++this.suggestReqId;

      const url = `https://nominatim.openstreetmap.org/search?format=jsonv2&limit=6&q=${encodeURIComponent(
        q + ', Novi Sad, Serbia'
      )}`;

      this.http.get<NominatimItem[]>(url).subscribe((items) => {
        if (reqId !== this.suggestReqId) return;

        this.patch({
          suggestions: (items ?? []).map((i) => ({
            ...i,
            display_name: this.toLatin(i.display_name),
          })),
        });
      });
    }, 250);
  }

  addFromSuggestion(s: NominatimItem) {
    const wp: Waypoint = {
      id: String(s.place_id),
      label: s.display_name,
      lat: Number(s.lat),
      lon: Number(s.lon),
    };

    this.patch({
      waypoints: [...this.waypoints, wp],
      query: '',
      suggestions: [],
    });
  }

  addFromMapClick(lat: number, lon: number) {
    if (!this.destOpen) return;

    const id = crypto.randomUUID();
    const fallback = `${lat.toFixed(5)}, ${lon.toFixed(5)}`;

    this.patch({
      waypoints: [...this.waypoints, { id, label: fallback, lat, lon }],
    });

    const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&accept-language=sr-Latn`;

    this.http.get<any>(url).subscribe({
      next: (res) => {
        const label = res?.display_name ? this.toLatin(res.display_name) : fallback;
        this.patch({
          waypoints: this.waypoints.map((w) => (w.id === id ? { ...w, label } : w)),
        });
      },
      error: () => {},
    });
  }

  removeWaypoint(id: string) {
    this.patch({ waypoints: this.waypoints.filter((w) => w.id !== id) });
  }

  resetDest() {
    this.patch({ waypoints: [], suggestions: [], query: '', destOpen: false });
  }

  private clearSuggestions() {
    this.patch({ suggestions: [] });
  }

  private patch(p: Partial<State>) {
    this.state$.next({ ...this.state$.value, ...p });
  }

  private toLatin(s: string) {
    const map: Record<string, string> = {
      А:'A',Б:'B',В:'V',Г:'G',Д:'D',Ђ:'Đ',Е:'E',Ж:'Ž',З:'Z',И:'I',Ј:'J',К:'K',Л:'L',Љ:'Lj',
      М:'M',Н:'N',Њ:'Nj',О:'O',П:'P',Р:'R',С:'S',Т:'T',Ћ:'Ć',У:'U',Ф:'F',Х:'H',Ц:'C',Ч:'Č',
      Џ:'Dž',Ш:'Š',а:'a',б:'b',в:'v',г:'g',д:'d',ђ:'đ',е:'e',ж:'ž',з:'z',и:'i',ј:'j',к:'k',
      л:'l',љ:'lj',м:'m',н:'n',њ:'nj',о:'o',п:'p',р:'r',с:'s',т:'t',ћ:'ć',у:'u',ф:'f',х:'h',
      ц:'c',ч:'č',џ:'dž',ш:'š',
    };
    return s.replace(/[\u0400-\u04FF]/g, (ch) => map[ch] ?? ch);
  }
}
