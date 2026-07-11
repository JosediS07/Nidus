import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent],
  template: `
    <div class="min-h-screen flex flex-col" style="background: var(--mat-sys-surface-dim)">
      <app-header></app-header>
      <main class="flex-1">
        <router-outlet />
      </main>
    </div>
  `
})
export class AppComponent {}
