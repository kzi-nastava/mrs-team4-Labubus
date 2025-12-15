import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Map } from './features/map/map';
import { Button } from './shared/ui/button/button';
import { IconButton } from './shared/ui/icon-button/icon-button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Map, Button, IconButton],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ubre');
}
