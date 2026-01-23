/*
    PURPOSE OF THIS SERVICE IS TO HANDLE THE GEOCODING PROCESS, 
    I.E. CONVERTING A USER'S QUERY INTO A LATITUDE AND LONGITUDE, 
    AND CONVERTING A LATITUDE AND LONGITUDE INTO A LABEL.
    THIS SERVICE IS USED IN THE RIDE PLANNING PROCESS.
    
    NOMINATIM SEARCH, NOMINATIM REVERSE, HTTP CLIENT, SEARCH(QUERY), REVERSE(LAT, LON)...
    NO BEHAVIOUR SUBJECT, STATE AND LEAFLET!
*/

import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { NominatimItem } from "./ride-types";

@Injectable({ providedIn: 'root' })
export class GeocodingService {
    private readonly http = inject(HttpClient);

    // convert cyrillic to latin
    public toLatin(s: string) {
        const map: Record<string, string> = {
          А:'A',Б:'B',В:'V',Г:'G',Д:'D',Ђ:'Đ',Е:'E',Ж:'Ž',З:'Z',И:'I',Ј:'J',К:'K',Л:'L',Љ:'Lj',
          М:'M',Н:'N',Њ:'Nj',О:'O',П:'P',Р:'R',С:'S',Т:'T',Ћ:'Ć',У:'U',Ф:'F',Х:'H',Ц:'C',Ч:'Č',
          Џ:'Dž',Ш:'Š',а:'a',б:'b',в:'v',г:'g',д:'d',ђ:'đ',е:'e',ж:'ž',з:'z',и:'i',ј:'j',к:'k',
          л:'l',љ:'lj',м:'m',н:'n',њ:'nj',о:'o',п:'p',р:'r',с:'s',т:'t',ћ:'ć',у:'u',ф:'f',х:'h',
          ц:'c',ч:'č',џ:'dž',ш:'š',
        };
        return s.replace(/[\u0400-\u04FF]/g, (ch) => map[ch] ?? ch);
      }

    // search by query and return suggestions
    search(query: string): Observable<NominatimItem[]> {
        const url = `https://nominatim.openstreetmap.org/search?format=jsonv2&limit=6&q=${encodeURIComponent(query + ', Novi Sad, Serbia')}`;
        return this.http.get<NominatimItem[]>(url).pipe(
            map((items) =>
                (items ?? []).map((i) => ({
                    ...i,
                    label: this.toLatin(i.display_name),
                }))
            )
        );
    }

    // reverse by latitude and longitude and return label
    reverse(lat: number, lon: number): Observable<string | null> { // null if there is unknown address or error
        const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&accept-language=sr-Latn`;
        return this.http.get<any>(url).pipe(
            map((result) => {
                const label = result?.display_name ? this.toLatin(result.display_name) : null;
                return label;
            })
        );
    }
}