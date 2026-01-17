import { inject, Injectable, NgZone } from "@angular/core";

// for example, admin approves profile change, and we need to send notification to driver with real time change 
import { UserDto } from '../dtos/user-dto';
import { AuthService } from "../features/auth/auth-service";
import { EventSourcePolyfill } from 'event-source-polyfill';

@Injectable({ providedIn: 'root' })
export class SseService {
  private es?: EventSourcePolyfill;
  private readonly authService = inject(AuthService);
  private readonly zone = inject(NgZone);

  connect(
    userId: number,
    onProfileChangeApproved: (user: UserDto) => void,
    onProfileChangeRejected: () => void
  ) {
    this.es?.close();
    const token = this.authService.getToken();
    this.es = new EventSourcePolyfill(`http://localhost:8080/api/users/sse?userId=${userId}`, { headers: { 'Authorization': `Bearer ${token}` }, heartbeatTimeout: 60000 });

    this.es.addEventListener('PROFILE_CHANGE_APPROVED', (e: any) => {
      const user = JSON.parse(e.data) as UserDto;
      this.zone.run(() => { onProfileChangeApproved(user); });
    });

    this.es.addEventListener('PROFILE_CHANGE_REJECTED', (e: any) => {
      this.zone.run(() => { onProfileChangeRejected(); });
    });

    this.es.onopen = () => {
      console.log('SSE connected');
    }; 

    this.es.addEventListener('PING', () => {
      console.log('SSE ping');
    });
    
    this.es.onerror = (err: any) => {
      console.warn('SSE error / reconnecting...:', err);
    };
  }

  disconnect() {
    this.es?.close();
    this.es = undefined;
  }
}


