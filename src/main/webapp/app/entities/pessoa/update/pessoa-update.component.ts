import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPessoa, Pessoa } from '../pessoa.model';
import { PessoaService } from '../service/pessoa.service';
import { ICondicao } from 'app/entities/condicao/condicao.model';
import { CondicaoService } from 'app/entities/condicao/service/condicao.service';
import { Sexo } from 'app/entities/enumerations/sexo.model';

@Component({
  selector: 'jhi-pessoa-update',
  templateUrl: './pessoa-update.component.html',
})
export class PessoaUpdateComponent implements OnInit {
  isSaving = false;
  sexoValues = Object.keys(Sexo);

  condicaosSharedCollection: ICondicao[] = [];

  editForm = this.fb.group({
    idPessoa: [],
    sexo: [],
    idade: [],
    condicao: [],
  });

  constructor(
    protected pessoaService: PessoaService,
    protected condicaoService: CondicaoService,
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
    if (pessoa.idPessoa !== undefined) {
      this.subscribeToSaveResponse(this.pessoaService.update(pessoa));
    } else {
      this.subscribeToSaveResponse(this.pessoaService.create(pessoa));
    }
  }

  trackCondicaoByIdCondicao(_index: number, item: ICondicao): number {
    return item.idCondicao!;
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
      idPessoa: pessoa.idPessoa,
      sexo: pessoa.sexo,
      idade: pessoa.idade,
      condicao: pessoa.condicao,
    });

    this.condicaosSharedCollection = this.condicaoService.addCondicaoToCollectionIfMissing(this.condicaosSharedCollection, pessoa.condicao);
  }

  protected loadRelationshipsOptions(): void {
    this.condicaoService
      .query()
      .pipe(map((res: HttpResponse<ICondicao[]>) => res.body ?? []))
      .pipe(
        map((condicaos: ICondicao[]) =>
          this.condicaoService.addCondicaoToCollectionIfMissing(condicaos, this.editForm.get('condicao')!.value)
        )
      )
      .subscribe((condicaos: ICondicao[]) => (this.condicaosSharedCollection = condicaos));
  }

  protected createFromForm(): IPessoa {
    return {
      ...new Pessoa(),
      idPessoa: this.editForm.get(['idPessoa'])!.value,
      sexo: this.editForm.get(['sexo'])!.value,
      idade: this.editForm.get(['idade'])!.value,
      condicao: this.editForm.get(['condicao'])!.value,
    };
  }
}
