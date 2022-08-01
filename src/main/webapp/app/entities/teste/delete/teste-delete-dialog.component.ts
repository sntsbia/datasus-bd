import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITeste } from '../teste.model';
import { TesteService } from '../service/teste.service';

@Component({
  templateUrl: './teste-delete-dialog.component.html',
})
export class TesteDeleteDialogComponent {
  teste?: ITeste;

  constructor(protected testeService: TesteService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.testeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
