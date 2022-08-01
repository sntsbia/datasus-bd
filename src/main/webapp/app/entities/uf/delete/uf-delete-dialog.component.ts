import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUf } from '../uf.model';
import { UfService } from '../service/uf.service';

@Component({
  templateUrl: './uf-delete-dialog.component.html',
})
export class UfDeleteDialogComponent {
  uf?: IUf;

  constructor(protected ufService: UfService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ufService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
