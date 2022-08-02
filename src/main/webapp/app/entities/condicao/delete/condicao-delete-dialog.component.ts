import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICondicao } from '../condicao.model';
import { CondicaoService } from '../service/condicao.service';

@Component({
  templateUrl: './condicao-delete-dialog.component.html',
})
export class CondicaoDeleteDialogComponent {
  condicao?: ICondicao;

  constructor(protected condicaoService: CondicaoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.condicaoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
