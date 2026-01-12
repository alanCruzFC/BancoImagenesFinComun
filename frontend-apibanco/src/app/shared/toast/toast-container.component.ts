import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Toast, ToastService } from './toast.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-toast-container',
  templateUrl: './toast-container.component.html',
  imports: [CommonModule],
})
export class ToastContainerComponent implements OnInit, OnDestroy {
  toasts: Toast[] = [];
  sub?: Subscription;
  

  constructor(private readonly toastService: ToastService) {}

  ngOnInit(): void {
    this.sub = this.toastService.toasts$.subscribe(list => {
      this.toasts = list;
    });
  }

  
  trackById = (_: number, t: Toast) => t.id;

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  dismiss(id: string) {
    this.toastService.dismiss(id);
  }

  iconFor(type: Toast['type']) {
    switch (type) {
      case 'success': return '✅';
      case 'info': return 'ℹ️';
      case 'warning': return '⚠️';
      case 'error': return '❌';
    }
  }
}
