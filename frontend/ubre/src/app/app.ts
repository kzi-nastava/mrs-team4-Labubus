import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Map } from './features/map/map';
import { ButtonComponent } from './shared/ui/button/button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Map, ButtonComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ubre');
}
