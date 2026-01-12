
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgIf } from "@angular/common"

@Component({
  selector: 'app-confirm-modal',
  templateUrl: './confirm-modal.component.html',
  imports: [NgIf],
})
export class ConfirmModalComponent {
  @Input() title = 'Confirmación';
  @Input() message = '¿Estás seguro de realizar esta acción?';
  @Input() confirmText = 'Confirmar';
  @Input() cancelText = 'Cancelar';
  @Input() show = false;

  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  onConfirm() {
    this.confirmed.emit();
    this.show = false;
  }

  onCancel() {
    this.cancelled.emit();
    this.show = false;
  }
}
