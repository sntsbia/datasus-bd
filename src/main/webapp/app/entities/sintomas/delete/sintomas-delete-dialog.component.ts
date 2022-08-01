import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISintomas } from '../sintomas.model';
import { SintomasService } from '../service/sintomas.service';

@Component({
  templateUrl: './sintomas-delete-dialog.component.html',
})
export class SintomasDeleteDialogComponent {
  sintomas?: ISintomas;

  constructor(protected sintomasService: SintomasService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sintomasService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
