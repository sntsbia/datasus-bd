import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITeste } from '../teste.model';
import { TesteService } from '../service/teste.service';
import { TesteDeleteDialogComponent } from '../delete/teste-delete-dialog.component';

@Component({
  selector: 'jhi-teste',
  templateUrl: './teste.component.html',
})
export class TesteComponent implements OnInit {
  testes?: ITeste[];
  isLoading = false;

  constructor(protected testeService: TesteService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.testeService.query().subscribe({
      next: (res: HttpResponse<ITeste[]>) => {
        this.isLoading = false;
        this.testes = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ITeste): number {
    return item.id!;
  }

  delete(teste: ITeste): void {
    const modalRef = this.modalService.open(TesteDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.teste = teste;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
