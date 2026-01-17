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
    
    this.es.onerror = (err: any) => {
      if (this.es?.readyState === EventSource.CLOSED) {
        console.error('SSE connection closed');
      } else if (this.es?.readyState === EventSource.CONNECTING) {
        console.log('SSE reconnecting...');
      } else {
        console.error('SSE error:', err);
      }
      this.es?.close();
      setTimeout(() => { 
        this.connect(userId, onProfileChangeApproved, onProfileChangeRejected);
      }, 1000);
    };

    this.es.onmessage = (e: any) => {
      console.log('SSE message:', e.data);
    };
  }

  disconnect() {
    this.es?.close();
    this.es = undefined;
  }
}


