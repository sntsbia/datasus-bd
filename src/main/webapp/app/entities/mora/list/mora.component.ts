import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMora } from '../mora.model';
import { MoraService } from '../service/mora.service';
import { MoraDeleteDialogComponent } from '../delete/mora-delete-dialog.component';

@Component({
  selector: 'jhi-mora',
  templateUrl: './mora.component.html',
})
export class MoraComponent implements OnInit {
  moras?: IMora[];
  isLoading = false;

  constructor(protected moraService: MoraService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.moraService.query().subscribe({
      next: (res: HttpResponse<IMora[]>) => {
        this.isLoading = false;
        this.moras = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IMora): number {
    return item.id!;
  }

  delete(mora: IMora): void {
    const modalRef = this.modalService.open(MoraDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.mora = mora;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
