import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVacina } from '../vacina.model';
import { VacinaService } from '../service/vacina.service';

@Component({
  templateUrl: './vacina-delete-dialog.component.html',
})
export class VacinaDeleteDialogComponent {
  vacina?: IVacina;

  constructor(protected vacinaService: VacinaService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.vacinaService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
