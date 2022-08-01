import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMora, Mora } from '../mora.model';
import { MoraService } from '../service/mora.service';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';
import { IMunicipio } from 'app/entities/municipio/municipio.model';
import { MunicipioService } from 'app/entities/municipio/service/municipio.service';

@Component({
  selector: 'jhi-mora-update',
  templateUrl: './mora-update.component.html',
})
export class MoraUpdateComponent implements OnInit {
  isSaving = false;

  pessoasSharedCollection: IPessoa[] = [];
  municipiosSharedCollection: IMunicipio[] = [];

  editForm = this.fb.group({
    id: [],
    pessoa: [],
    municipio: [],
  });

  constructor(
    protected moraService: MoraService,
    protected pessoaService: PessoaService,
    protected municipioService: MunicipioService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mora }) => {
      this.updateForm(mora);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const mora = this.createFromForm();
    if (mora.id !== undefined) {
      this.subscribeToSaveResponse(this.moraService.update(mora));
    } else {
      this.subscribeToSaveResponse(this.moraService.create(mora));
    }
  }

  trackPessoaById(_index: number, item: IPessoa): number {
    return item.id!;
  }

  trackMunicipioById(_index: number, item: IMunicipio): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMora>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(mora: IMora): void {
    this.editForm.patchValue({
      id: mora.id,
      pessoa: mora.pessoa,
      municipio: mora.municipio,
    });

    this.pessoasSharedCollection = this.pessoaService.addPessoaToCollectionIfMissing(this.pessoasSharedCollection, mora.pessoa);
    this.municipiosSharedCollection = this.municipioService.addMunicipioToCollectionIfMissing(
      this.municipiosSharedCollection,
      mora.municipio
    );
  }

  protected loadRelationshipsOptions(): void {
    this.pessoaService
      .query()
      .pipe(map((res: HttpResponse<IPessoa[]>) => res.body ?? []))
      .pipe(map((pessoas: IPessoa[]) => this.pessoaService.addPessoaToCollectionIfMissing(pessoas, this.editForm.get('pessoa')!.value)))
      .subscribe((pessoas: IPessoa[]) => (this.pessoasSharedCollection = pessoas));

    this.municipioService
      .query()
      .pipe(map((res: HttpResponse<IMunicipio[]>) => res.body ?? []))
      .pipe(
        map((municipios: IMunicipio[]) =>
          this.municipioService.addMunicipioToCollectionIfMissing(municipios, this.editForm.get('municipio')!.value)
        )
      )
      .subscribe((municipios: IMunicipio[]) => (this.municipiosSharedCollection = municipios));
  }

  protected createFromForm(): IMora {
    return {
      ...new Mora(),
      id: this.editForm.get(['id'])!.value,
      pessoa: this.editForm.get(['pessoa'])!.value,
      municipio: this.editForm.get(['municipio'])!.value,
    };
  }
}
