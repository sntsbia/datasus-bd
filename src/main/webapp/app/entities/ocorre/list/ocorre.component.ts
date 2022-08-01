import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOcorre } from '../ocorre.model';
import { OcorreService } from '../service/ocorre.service';
import { OcorreDeleteDialogComponent } from '../delete/ocorre-delete-dialog.component';

@Component({
  selector: 'jhi-ocorre',
  templateUrl: './ocorre.component.html',
})
export class OcorreComponent implements OnInit {
  ocorres?: IOcorre[];
  isLoading = false;

  constructor(protected ocorreService: OcorreService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.ocorreService.query().subscribe({
      next: (res: HttpResponse<IOcorre[]>) => {
        this.isLoading = false;
        this.ocorres = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IOcorre): number {
    return item.id!;
  }

  delete(ocorre: IOcorre): void {
    const modalRef = this.modalService.open(OcorreDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ocorre = ocorre;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
