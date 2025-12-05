import { NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-toast',
  template: `
    <div class="flex items-center w-full max-w-sm p-4 rounded-lg shadow-lg border transition transform animate-fade-in-up"
         [ngClass]="typeClasses" role="alert">
      <div class="inline-flex items-center justify-center shrink-0 w-7 h-7 rounded" [ngClass]="iconBg">
        <ng-content select="[icon]"></ng-content>
      </div>
      <div class="ms-3 text-sm font-normal">{{ message }}</div>
      <button type="button" class="ms-auto text-gray-500 hover:text-gray-700 focus:outline-none"
              (click)="close()">
        ✖
      </button>
    </div>
  `,
  styles: [`
    .animate-fade-in-up {
      animation: fadeInUp 0.4s ease forwards;
    }
    @keyframes fadeInUp {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `],
  imports: [NgClass]
})
export class ToastComponent {
  @Input() message = '';
  @Input() type: 'success' | 'error' = 'success';

  get typeClasses() {
    return this.type === 'success'
      ? 'bg-green-50 border-green-300 text-green-700'
      : 'bg-red-50 border-red-300 text-red-700';
  }

  get iconBg() {
    return this.type === 'success'
      ? 'bg-green-100 text-green-600'
      : 'bg-red-100 text-red-600';
  }

  close() {
    // Aquí puedes emitir un evento para que el servicio lo elimine
  }
}
