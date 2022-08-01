import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICondicoes } from '../condicoes.model';
import { CondicoesService } from '../service/condicoes.service';

@Component({
  templateUrl: './condicoes-delete-dialog.component.html',
})
export class CondicoesDeleteDialogComponent {
  condicoes?: ICondicoes;

  constructor(protected condicoesService: CondicoesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.condicoesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
