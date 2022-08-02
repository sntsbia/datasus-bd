import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMunicipio } from '../municipio.model';
import { MunicipioService } from '../service/municipio.service';

@Component({
  templateUrl: './municipio-delete-dialog.component.html',
})
export class MunicipioDeleteDialogComponent {
  municipio?: IMunicipio;

  constructor(protected municipioService: MunicipioService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.municipioService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
