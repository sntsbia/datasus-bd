import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOcorre } from '../ocorre.model';
import { OcorreService } from '../service/ocorre.service';

@Component({
  templateUrl: './ocorre-delete-dialog.component.html',
})
export class OcorreDeleteDialogComponent {
  ocorre?: IOcorre;

  constructor(protected ocorreService: OcorreService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ocorreService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
