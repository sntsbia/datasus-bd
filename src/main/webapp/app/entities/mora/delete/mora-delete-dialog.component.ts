import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMora } from '../mora.model';
import { MoraService } from '../service/mora.service';

@Component({
  templateUrl: './mora-delete-dialog.component.html',
})
export class MoraDeleteDialogComponent {
  mora?: IMora;

  constructor(protected moraService: MoraService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.moraService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
