import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICondicoes } from '../condicoes.model';
import { CondicoesService } from '../service/condicoes.service';
import { CondicoesDeleteDialogComponent } from '../delete/condicoes-delete-dialog.component';

@Component({
  selector: 'jhi-condicoes',
  templateUrl: './condicoes.component.html',
})
export class CondicoesComponent implements OnInit {
  condicoes?: ICondicoes[];
  isLoading = false;

  constructor(protected condicoesService: CondicoesService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.condicoesService.query().subscribe({
      next: (res: HttpResponse<ICondicoes[]>) => {
        this.isLoading = false;
        this.condicoes = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ICondicoes): number {
    return item.id!;
  }

  delete(condicoes: ICondicoes): void {
    const modalRef = this.modalService.open(CondicoesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.condicoes = condicoes;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
