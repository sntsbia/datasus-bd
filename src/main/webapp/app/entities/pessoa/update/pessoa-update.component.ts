import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPessoa, Pessoa } from '../pessoa.model';
import { PessoaService } from '../service/pessoa.service';
import { ICondicoes } from 'app/entities/condicoes/condicoes.model';
import { CondicoesService } from 'app/entities/condicoes/service/condicoes.service';
import { Sexo } from 'app/entities/enumerations/sexo.model';

@Component({
  selector: 'jhi-pessoa-update',
  templateUrl: './pessoa-update.component.html',
})
export class PessoaUpdateComponent implements OnInit {
  isSaving = false;
  sexoValues = Object.keys(Sexo);

  condicoesSharedCollection: ICondicoes[] = [];

  editForm = this.fb.group({
    id: [],
    sexo: [],
    idade: [],
    condicoes: [],
  });

  constructor(
    protected pessoaService: PessoaService,
    protected condicoesService: CondicoesService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pessoa }) => {
      this.updateForm(pessoa);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const pessoa = this.createFromForm();
    if (pessoa.id !== undefined) {
      this.subscribeToSaveResponse(this.pessoaService.update(pessoa));
    } else {
      this.subscribeToSaveResponse(this.pessoaService.create(pessoa));
    }
  }

  trackCondicoesById(_index: number, item: ICondicoes): number {
    return item.id!;
  }

  getSelectedCondicoes(option: ICondicoes, selectedVals?: ICondicoes[]): ICondicoes {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPessoa>>): void {
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

  protected updateForm(pessoa: IPessoa): void {
    this.editForm.patchValue({
      id: pessoa.id,
      sexo: pessoa.sexo,
      idade: pessoa.idade,
      condicoes: pessoa.condicoes,
    });

    this.condicoesSharedCollection = this.condicoesService.addCondicoesToCollectionIfMissing(
      this.condicoesSharedCollection,
      ...(pessoa.condicoes ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.condicoesService
      .query()
      .pipe(map((res: HttpResponse<ICondicoes[]>) => res.body ?? []))
      .pipe(
        map((condicoes: ICondicoes[]) =>
          this.condicoesService.addCondicoesToCollectionIfMissing(condicoes, ...(this.editForm.get('condicoes')!.value ?? []))
        )
      )
      .subscribe((condicoes: ICondicoes[]) => (this.condicoesSharedCollection = condicoes));
  }

  protected createFromForm(): IPessoa {
    return {
      ...new Pessoa(),
      id: this.editForm.get(['id'])!.value,
      sexo: this.editForm.get(['sexo'])!.value,
      idade: this.editForm.get(['idade'])!.value,
      condicoes: this.editForm.get(['condicoes'])!.value,
    };
  }
}
