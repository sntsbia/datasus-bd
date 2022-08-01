import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISintomas } from '../sintomas.model';
import { SintomasService } from '../service/sintomas.service';
import { SintomasDeleteDialogComponent } from '../delete/sintomas-delete-dialog.component';

@Component({
  selector: 'jhi-sintomas',
  templateUrl: './sintomas.component.html',
})
export class SintomasComponent implements OnInit {
  sintomas?: ISintomas[];
  isLoading = false;

  constructor(protected sintomasService: SintomasService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.sintomasService.query().subscribe({
      next: (res: HttpResponse<ISintomas[]>) => {
        this.isLoading = false;
        this.sintomas = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ISintomas): number {
    return item.id!;
  }

  delete(sintomas: ISintomas): void {
    const modalRef = this.modalService.open(SintomasDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.sintomas = sintomas;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
