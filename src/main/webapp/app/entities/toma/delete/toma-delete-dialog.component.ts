import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IToma } from '../toma.model';
import { TomaService } from '../service/toma.service';

@Component({
  templateUrl: './toma-delete-dialog.component.html',
})
export class TomaDeleteDialogComponent {
  toma?: IToma;

  constructor(protected tomaService: TomaService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tomaService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
