import { Injectable, NgZone, inject } from '@angular/core';
import { Client, IMessage, StompHeaders, StompSubscription } from '@stomp/stompjs';
import { BehaviorSubject, filter, Observable, Subject, take } from 'rxjs';
import SockJS from 'sockjs-client/dist/sockjs';
import { AuthService } from '../features/auth/auth-service';
import { ProfileChangeNotification } from '../notifications/profile-change-notification';

export type WebSocketConnectionState = 'disconnected' | 'connecting' | 'connected';

interface QueuedMessage {
  destination: string;
  body: unknown;
  headers?: StompHeaders;
}

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private readonly authService = inject(AuthService);
  private readonly zone = inject(NgZone);

  private client?: Client;
  private subscriptions = new Map<string, StompSubscription>();
  private topicSubjects = new Map<string, Subject<IMessage>>();
  private sendQueue: QueuedMessage[] = [];
  private readonly maxQueueSize = 100;

  private readonly connectionStateSubject = new BehaviorSubject<WebSocketConnectionState>('disconnected');
  readonly connectionState$ = this.connectionStateSubject.asObservable();

  connect(): void {
    if (this.client) {
      if (!this.client.active) {
        this.connectionStateSubject.next('connecting');
        this.client.activate();
      }
      return;
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS(`${this.authService.apiHost}ws`),
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      debug: () => {},
    });

    this.client.beforeConnect = () => {
      this.client!.connectHeaders = this.buildHeaders();
    };

    this.client.onConnect = () => {
      this.connectionStateSubject.next('connected');
      this.resubscribeTopics();
      this.flushQueue();
    };

    this.client.onDisconnect = () => {
      this.connectionStateSubject.next('disconnected');
      this.clearSubscriptions();
    };

    this.client.onStompError = () => {
      this.connectionStateSubject.next('disconnected');
      this.clearSubscriptions();
    };

    this.client.onWebSocketClose = () => {
      this.connectionStateSubject.next('disconnected');
      this.clearSubscriptions();
    };

    this.connectionStateSubject.next('connecting');
    this.client.activate();
  }

  disconnect(): void {
    this.clearSubscriptions();
    this.sendQueue = [];
    this.client?.deactivate();
    this.client = undefined;
    this.connectionStateSubject.next('disconnected');
  }

  send(destination: string, body: unknown, headers: StompHeaders = {}): void {
    const message: QueuedMessage = { destination, body, headers };
    if (this.client?.connected) {
      this.publish(message);
      return;
    }

    this.enqueueMessage(message);
    this.connect();
  }

  profileChangeNotifications(userId: number): Observable<ProfileChangeNotification> {
    const topic = `/topic/profile-changes/${userId}`;
    return new Observable<ProfileChangeNotification>((subscriber) => {
      const topic$ = this.listenToTopic(topic);
      const subscription = topic$.subscribe({
        next: (message) => {
          try {
            const payload = JSON.parse(message.body) as ProfileChangeNotification;
            this.zone.run(() => subscriber.next(payload));
          } catch (error) {
            this.zone.run(() => subscriber.error(error));
          }
        },
        error: (error) => this.zone.run(() => subscriber.error(error)),
      });

      return () => subscription.unsubscribe();
    });
  }

  private listenToTopic(topic: string): Observable<IMessage> {
    let subject = this.topicSubjects.get(topic);
    if (!subject) {
      subject = new Subject<IMessage>();
      this.topicSubjects.set(topic, subject);
      this.connect();
    }

    this.connectionState$
      .pipe(
        filter((state) => state === 'connected'),
        take(1)
      )
      .subscribe(() => this.subscribeTopic(topic));

    return subject.asObservable();
  }

  private subscribeTopic(topic: string): void {
    if (!this.client?.connected || this.subscriptions.has(topic)) {
      return;
    }

    const subject = this.topicSubjects.get(topic);
    if (!subject) {
      return;
    }

    const subscription = this.client.subscribe(topic, (message) => {
      this.zone.run(() => subject.next(message));
    });
    this.subscriptions.set(topic, subscription);
  }

  private resubscribeTopics(): void {
    this.clearSubscriptions();
    this.topicSubjects.forEach((_, topic) => this.subscribeTopic(topic));
  }

  private clearSubscriptions(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    this.subscriptions.clear();
  }

  private buildHeaders(): StompHeaders {
    const token = this.authService.getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  private flushQueue(): void {
    if (!this.client?.connected) {
      return;
    }

    while (this.sendQueue.length > 0) {
      const message = this.sendQueue.shift();
      if (message) {
        this.publish(message);
      }
    }
  }

  private publish(message: QueuedMessage): void {
    this.client?.publish({
      destination: message.destination,
      body: JSON.stringify(message.body),
      headers: message.headers,
    });
  }

  private enqueueMessage(message: QueuedMessage): void {
    if (this.sendQueue.length >= this.maxQueueSize) {
      this.sendQueue.shift();
    }
    this.sendQueue.push(message);
  }
}
