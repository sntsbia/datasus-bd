import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUf } from '../uf.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-uf-detail',
  templateUrl: './uf-detail.component.html',
})
export class UfDetailComponent implements OnInit {
  uf: IUf | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ uf }) => {
      this.uf = uf;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
