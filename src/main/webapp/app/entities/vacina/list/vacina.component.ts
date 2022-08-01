import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IVacina } from '../vacina.model';
import { VacinaService } from '../service/vacina.service';
import { VacinaDeleteDialogComponent } from '../delete/vacina-delete-dialog.component';

@Component({
  selector: 'jhi-vacina',
  templateUrl: './vacina.component.html',
})
export class VacinaComponent implements OnInit {
  vacinas?: IVacina[];
  isLoading = false;

  constructor(protected vacinaService: VacinaService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.vacinaService.query().subscribe({
      next: (res: HttpResponse<IVacina[]>) => {
        this.isLoading = false;
        this.vacinas = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IVacina): number {
    return item.id!;
  }

  delete(vacina: IVacina): void {
    const modalRef = this.modalService.open(VacinaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.vacina = vacina;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
