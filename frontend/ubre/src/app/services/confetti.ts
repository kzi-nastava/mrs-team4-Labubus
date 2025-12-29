import { Injectable } from '@angular/core';
import confetti from 'canvas-confetti';

@Injectable({ providedIn: 'root' })
export class ConfettiService {
  fire() {
    confetti({
      particleCount: 120,
      spread: 70,
      startVelocity: 45,
      origin: { y: 0.75 }
    });
  }
}
