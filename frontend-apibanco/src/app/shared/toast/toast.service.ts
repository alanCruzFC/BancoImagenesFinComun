import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type ToastType = 'success' | 'info' | 'warning' | 'error';

export interface Toast {
  id: string;
  type: ToastType;
  title?: string;
  message: string;
  timeoutMs?: number;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private readonly toastsSubject = new BehaviorSubject<Toast[]>([]);
  toasts$ = this.toastsSubject.asObservable();

    show(message: string, type: ToastType = 'info', options?: { title?: string; timeoutMs?: number }) {
    const toast: Toast = {
        id: crypto.randomUUID(),
        type,
        title: options?.title,
        message,
        timeoutMs: options?.timeoutMs ?? 4000,
    };

    const current = this.toastsSubject.getValue();
    // Inserta al inicio para que el más nuevo quede arriba
    this.toastsSubject.next([toast, ...current]);

    if (toast.timeoutMs && toast.timeoutMs > 0) {
        setTimeout(() => this.dismiss(toast.id), toast.timeoutMs);
    }
    }


  success(message: string, title?: string, timeoutMs?: number) {
    this.show(message, 'success', { title, timeoutMs });
  }
  info(message: string, title?: string, timeoutMs?: number) {
    this.show(message, 'info', { title, timeoutMs });
  }
  warning(message: string, title?: string, timeoutMs?: number) {
    this.show(message, 'warning', { title, timeoutMs });
  }
  error(message: string, title?: string, timeoutMs?: number) {
    this.show(message, 'error', { title, timeoutMs });
  }

  dismiss(id: string) {
    const current = this.toastsSubject.getValue();
    this.toastsSubject.next(current.filter(t => t.id !== id));
  }

  clearAll() {
    this.toastsSubject.next([]);
  }
}
