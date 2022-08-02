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
import { ISintomas } from 'app/entities/sintomas/sintomas.model';
import { SintomasService } from 'app/entities/sintomas/service/sintomas.service';

@Component({
  selector: 'jhi-teste-update',
  templateUrl: './teste-update.component.html',
})
export class TesteUpdateComponent implements OnInit {
  isSaving = false;

  pessoasSharedCollection: IPessoa[] = [];
  sintomasSharedCollection: ISintomas[] = [];

  editForm = this.fb.group({
    idTeste: [],
    dataTeste: [],
    resultado: [],
    fkIdTeste: [],
    sintomas: [],
  });

  constructor(
    protected testeService: TesteService,
    protected pessoaService: PessoaService,
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
    if (teste.idTeste !== undefined) {
      this.subscribeToSaveResponse(this.testeService.update(teste));
    } else {
      this.subscribeToSaveResponse(this.testeService.create(teste));
    }
  }

  trackPessoaByIdPessoa(_index: number, item: IPessoa): number {
    return item.idPessoa!;
  }

  trackSintomasByIdSintomas(_index: number, item: ISintomas): number {
    return item.idSintomas!;
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
      idTeste: teste.idTeste,
      dataTeste: teste.dataTeste,
      resultado: teste.resultado,
      fkIdTeste: teste.fkIdTeste,
      sintomas: teste.sintomas,
    });

    this.pessoasSharedCollection = this.pessoaService.addPessoaToCollectionIfMissing(this.pessoasSharedCollection, teste.fkIdTeste);
    this.sintomasSharedCollection = this.sintomasService.addSintomasToCollectionIfMissing(this.sintomasSharedCollection, teste.sintomas);
  }

  protected loadRelationshipsOptions(): void {
    this.pessoaService
      .query()
      .pipe(map((res: HttpResponse<IPessoa[]>) => res.body ?? []))
      .pipe(map((pessoas: IPessoa[]) => this.pessoaService.addPessoaToCollectionIfMissing(pessoas, this.editForm.get('fkIdTeste')!.value)))
      .subscribe((pessoas: IPessoa[]) => (this.pessoasSharedCollection = pessoas));

    this.sintomasService
      .query()
      .pipe(map((res: HttpResponse<ISintomas[]>) => res.body ?? []))
      .pipe(
        map((sintomas: ISintomas[]) =>
          this.sintomasService.addSintomasToCollectionIfMissing(sintomas, this.editForm.get('sintomas')!.value)
        )
      )
      .subscribe((sintomas: ISintomas[]) => (this.sintomasSharedCollection = sintomas));
  }

  protected createFromForm(): ITeste {
    return {
      ...new Teste(),
      idTeste: this.editForm.get(['idTeste'])!.value,
      dataTeste: this.editForm.get(['dataTeste'])!.value,
      resultado: this.editForm.get(['resultado'])!.value,
      fkIdTeste: this.editForm.get(['fkIdTeste'])!.value,
      sintomas: this.editForm.get(['sintomas'])!.value,
    };
  }
}
