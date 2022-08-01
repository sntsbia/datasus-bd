import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITeste, Teste } from '../teste.model';
import { TesteService } from '../service/teste.service';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';
import { IMunicipio } from 'app/entities/municipio/municipio.model';
import { MunicipioService } from 'app/entities/municipio/service/municipio.service';
import { ISintomas } from 'app/entities/sintomas/sintomas.model';
import { SintomasService } from 'app/entities/sintomas/service/sintomas.service';

@Component({
  selector: 'jhi-teste-update',
  templateUrl: './teste-update.component.html',
})
export class TesteUpdateComponent implements OnInit {
  isSaving = false;

  pessoasSharedCollection: IPessoa[] = [];
  municipiosSharedCollection: IMunicipio[] = [];
  sintomasSharedCollection: ISintomas[] = [];

  editForm = this.fb.group({
    id: [],
    dataTeste: [],
    resultado: [],
    pessoa: [],
    municipio: [],
    sintomas: [],
  });

  constructor(
    protected testeService: TesteService,
    protected pessoaService: PessoaService,
    protected municipioService: MunicipioService,
    protected sintomasService: SintomasService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teste }) => {
      this.updateForm(teste);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const teste = this.createFromForm();
    if (teste.id !== undefined) {
      this.subscribeToSaveResponse(this.testeService.update(teste));
    } else {
      this.subscribeToSaveResponse(this.testeService.create(teste));
    }
  }

  trackPessoaById(_index: number, item: IPessoa): number {
    return item.id!;
  }

  trackMunicipioById(_index: number, item: IMunicipio): number {
    return item.id!;
  }

  trackSintomasById(_index: number, item: ISintomas): number {
    return item.id!;
  }

  getSelectedSintomas(option: ISintomas, selectedVals?: ISintomas[]): ISintomas {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeste>>): void {
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

  protected updateForm(teste: ITeste): void {
    this.editForm.patchValue({
      id: teste.id,
      dataTeste: teste.dataTeste,
      resultado: teste.resultado,
      pessoa: teste.pessoa,
      municipio: teste.municipio,
      sintomas: teste.sintomas,
    });

    this.pessoasSharedCollection = this.pessoaService.addPessoaToCollectionIfMissing(this.pessoasSharedCollection, teste.pessoa);
    this.municipiosSharedCollection = this.municipioService.addMunicipioToCollectionIfMissing(
      this.municipiosSharedCollection,
      teste.municipio
    );
    this.sintomasSharedCollection = this.sintomasService.addSintomasToCollectionIfMissing(
      this.sintomasSharedCollection,
      ...(teste.sintomas ?? [])
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

    this.sintomasService
      .query()
      .pipe(map((res: HttpResponse<ISintomas[]>) => res.body ?? []))
      .pipe(
        map((sintomas: ISintomas[]) =>
          this.sintomasService.addSintomasToCollectionIfMissing(sintomas, ...(this.editForm.get('sintomas')!.value ?? []))
        )
      )
      .subscribe((sintomas: ISintomas[]) => (this.sintomasSharedCollection = sintomas));
  }

  protected createFromForm(): ITeste {
    return {
      ...new Teste(),
      id: this.editForm.get(['id'])!.value,
      dataTeste: this.editForm.get(['dataTeste'])!.value,
      resultado: this.editForm.get(['resultado'])!.value,
      pessoa: this.editForm.get(['pessoa'])!.value,
      municipio: this.editForm.get(['municipio'])!.value,
      sintomas: this.editForm.get(['sintomas'])!.value,
    };
  }
}
