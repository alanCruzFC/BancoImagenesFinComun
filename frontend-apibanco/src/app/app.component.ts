import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { ToastContainerComponent } from "./shared/toast/toast-container.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastContainerComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent implements OnInit {
  protected readonly title = signal('frontend-apibanco');
  private readonly isBrowser: boolean;

  constructor( private readonly router: Router, 
    @Inject(PLATFORM_ID) platformId: Object 
  ) { 
    this.isBrowser = isPlatformBrowser(platformId); 
    if (this.isBrowser) {
      this.router.events.pipe( 
        filter((e): e is NavigationEnd => e instanceof NavigationEnd) 
      ).subscribe((e) => { 
        try { globalThis.localStorage.setItem('ultimaRuta', e.urlAfterRedirects); 

        } catch {} 
      }); 
    } 
  }

  ngOnInit(): void { 
    if (this.isBrowser) { 
      try { const ultimaRuta = globalThis.localStorage.getItem('ultimaRuta'); 
        if (ultimaRuta) { 
          this.router.navigateByUrl(ultimaRuta);
        } 
      } catch {

      } 
    } 
  }
}
