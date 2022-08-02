import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMunicipio, Municipio } from '../municipio.model';
import { MunicipioService } from '../service/municipio.service';
import { IUf } from 'app/entities/uf/uf.model';
import { UfService } from 'app/entities/uf/service/uf.service';

@Component({
  selector: 'jhi-municipio-update',
  templateUrl: './municipio-update.component.html',
})
export class MunicipioUpdateComponent implements OnInit {
  isSaving = false;

  ufsSharedCollection: IUf[] = [];

  editForm = this.fb.group({
    idMunicipio: [],
    municipio: [],
    fkIdUf: [],
  });

  constructor(
    protected municipioService: MunicipioService,
    protected ufService: UfService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ municipio }) => {
      this.updateForm(municipio);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const municipio = this.createFromForm();
    if (municipio.idMunicipio !== undefined) {
      this.subscribeToSaveResponse(this.municipioService.update(municipio));
    } else {
      this.subscribeToSaveResponse(this.municipioService.create(municipio));
    }
  }

  trackUfByIdUf(_index: number, item: IUf): number {
    return item.idUf!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMunicipio>>): void {
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

  protected updateForm(municipio: IMunicipio): void {
    this.editForm.patchValue({
      idMunicipio: municipio.idMunicipio,
      municipio: municipio.municipio,
      fkIdUf: municipio.fkIdUf,
    });

    this.ufsSharedCollection = this.ufService.addUfToCollectionIfMissing(this.ufsSharedCollection, municipio.fkIdUf);
  }

  protected loadRelationshipsOptions(): void {
    this.ufService
      .query()
      .pipe(map((res: HttpResponse<IUf[]>) => res.body ?? []))
      .pipe(map((ufs: IUf[]) => this.ufService.addUfToCollectionIfMissing(ufs, this.editForm.get('fkIdUf')!.value)))
      .subscribe((ufs: IUf[]) => (this.ufsSharedCollection = ufs));
  }

  protected createFromForm(): IMunicipio {
    return {
      ...new Municipio(),
      idMunicipio: this.editForm.get(['idMunicipio'])!.value,
      municipio: this.editForm.get(['municipio'])!.value,
      fkIdUf: this.editForm.get(['fkIdUf'])!.value,
    };
  }
}
