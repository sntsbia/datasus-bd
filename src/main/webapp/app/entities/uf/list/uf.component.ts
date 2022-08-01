import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUf } from '../uf.model';
import { UfService } from '../service/uf.service';
import { UfDeleteDialogComponent } from '../delete/uf-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-uf',
  templateUrl: './uf.component.html',
})
export class UfComponent implements OnInit {
  ufs?: IUf[];
  isLoading = false;

  constructor(protected ufService: UfService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.ufService.query().subscribe({
      next: (res: HttpResponse<IUf[]>) => {
        this.isLoading = false;
        this.ufs = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IUf): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(uf: IUf): void {
    const modalRef = this.modalService.open(UfDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.uf = uf;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
