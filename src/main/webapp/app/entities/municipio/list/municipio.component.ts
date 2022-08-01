import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMunicipio } from '../municipio.model';
import { MunicipioService } from '../service/municipio.service';
import { MunicipioDeleteDialogComponent } from '../delete/municipio-delete-dialog.component';

@Component({
  selector: 'jhi-municipio',
  templateUrl: './municipio.component.html',
})
export class MunicipioComponent implements OnInit {
  municipios?: IMunicipio[];
  isLoading = false;

  constructor(protected municipioService: MunicipioService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.municipioService.query().subscribe({
      next: (res: HttpResponse<IMunicipio[]>) => {
        this.isLoading = false;
        this.municipios = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IMunicipio): number {
    return item.id!;
  }

  delete(municipio: IMunicipio): void {
    const modalRef = this.modalService.open(MunicipioDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.municipio = municipio;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
