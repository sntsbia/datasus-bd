import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IToma } from '../toma.model';
import { TomaService } from '../service/toma.service';
import { TomaDeleteDialogComponent } from '../delete/toma-delete-dialog.component';

@Component({
  selector: 'jhi-toma',
  templateUrl: './toma.component.html',
})
export class TomaComponent implements OnInit {
  tomas?: IToma[];
  isLoading = false;

  constructor(protected tomaService: TomaService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.tomaService.query().subscribe({
      next: (res: HttpResponse<IToma[]>) => {
        this.isLoading = false;
        this.tomas = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IToma): number {
    return item.id!;
  }

  delete(toma: IToma): void {
    const modalRef = this.modalService.open(TomaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.toma = toma;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
