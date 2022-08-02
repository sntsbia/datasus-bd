import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IOcorre, Ocorre } from '../ocorre.model';
import { OcorreService } from '../service/ocorre.service';
import { ITeste } from 'app/entities/teste/teste.model';
import { TesteService } from 'app/entities/teste/service/teste.service';
import { IMunicipio } from 'app/entities/municipio/municipio.model';
import { MunicipioService } from 'app/entities/municipio/service/municipio.service';

@Component({
  selector: 'jhi-ocorre-update',
  templateUrl: './ocorre-update.component.html',
})
export class OcorreUpdateComponent implements OnInit {
  isSaving = false;

  testesSharedCollection: ITeste[] = [];
  municipiosSharedCollection: IMunicipio[] = [];

  editForm = this.fb.group({
    id: [],
    fkIdTeste: [],
    fkIdMunicipio: [],
  });

  constructor(
    protected ocorreService: OcorreService,
    protected testeService: TesteService,
    protected municipioService: MunicipioService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ocorre }) => {
      this.updateForm(ocorre);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ocorre = this.createFromForm();
    if (ocorre.id !== undefined) {
      this.subscribeToSaveResponse(this.ocorreService.update(ocorre));
    } else {
      this.subscribeToSaveResponse(this.ocorreService.create(ocorre));
    }
  }

  trackTesteByIdTeste(_index: number, item: ITeste): number {
    return item.idTeste!;
  }

  trackMunicipioByIdMunicipio(_index: number, item: IMunicipio): number {
    return item.idMunicipio!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOcorre>>): void {
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

  protected updateForm(ocorre: IOcorre): void {
    this.editForm.patchValue({
      id: ocorre.id,
      fkIdTeste: ocorre.fkIdTeste,
      fkIdMunicipio: ocorre.fkIdMunicipio,
    });

    this.testesSharedCollection = this.testeService.addTesteToCollectionIfMissing(this.testesSharedCollection, ocorre.fkIdTeste);
    this.municipiosSharedCollection = this.municipioService.addMunicipioToCollectionIfMissing(
      this.municipiosSharedCollection,
      ocorre.fkIdMunicipio
    );
  }

  protected loadRelationshipsOptions(): void {
    this.testeService
      .query()
      .pipe(map((res: HttpResponse<ITeste[]>) => res.body ?? []))
      .pipe(map((testes: ITeste[]) => this.testeService.addTesteToCollectionIfMissing(testes, this.editForm.get('fkIdTeste')!.value)))
      .subscribe((testes: ITeste[]) => (this.testesSharedCollection = testes));

    this.municipioService
      .query()
      .pipe(map((res: HttpResponse<IMunicipio[]>) => res.body ?? []))
      .pipe(
        map((municipios: IMunicipio[]) =>
          this.municipioService.addMunicipioToCollectionIfMissing(municipios, this.editForm.get('fkIdMunicipio')!.value)
        )
      )
      .subscribe((municipios: IMunicipio[]) => (this.municipiosSharedCollection = municipios));
  }

  protected createFromForm(): IOcorre {
    return {
      ...new Ocorre(),
      id: this.editForm.get(['id'])!.value,
      fkIdTeste: this.editForm.get(['fkIdTeste'])!.value,
      fkIdMunicipio: this.editForm.get(['fkIdMunicipio'])!.value,
    };
  }
}
