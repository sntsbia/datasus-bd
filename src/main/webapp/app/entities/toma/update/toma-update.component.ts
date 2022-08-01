import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IToma, Toma } from '../toma.model';
import { TomaService } from '../service/toma.service';
import { IVacina } from 'app/entities/vacina/vacina.model';
import { VacinaService } from 'app/entities/vacina/service/vacina.service';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';

@Component({
  selector: 'jhi-toma-update',
  templateUrl: './toma-update.component.html',
})
export class TomaUpdateComponent implements OnInit {
  isSaving = false;

  vacinasSharedCollection: IVacina[] = [];
  pessoasSharedCollection: IPessoa[] = [];

  editForm = this.fb.group({
    id: [],
    data: [],
    lote: [],
    dose: [],
    vacina: [],
    pessoa: [],
  });

  constructor(
    protected tomaService: TomaService,
    protected vacinaService: VacinaService,
    protected pessoaService: PessoaService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ toma }) => {
      this.updateForm(toma);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const toma = this.createFromForm();
    if (toma.id !== undefined) {
      this.subscribeToSaveResponse(this.tomaService.update(toma));
    } else {
      this.subscribeToSaveResponse(this.tomaService.create(toma));
    }
  }

  trackVacinaById(_index: number, item: IVacina): number {
    return item.id!;
  }

  trackPessoaById(_index: number, item: IPessoa): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IToma>>): void {
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

  protected updateForm(toma: IToma): void {
    this.editForm.patchValue({
      id: toma.id,
      data: toma.data,
      lote: toma.lote,
      dose: toma.dose,
      vacina: toma.vacina,
      pessoa: toma.pessoa,
    });

    this.vacinasSharedCollection = this.vacinaService.addVacinaToCollectionIfMissing(this.vacinasSharedCollection, toma.vacina);
    this.pessoasSharedCollection = this.pessoaService.addPessoaToCollectionIfMissing(this.pessoasSharedCollection, toma.pessoa);
  }

  protected loadRelationshipsOptions(): void {
    this.vacinaService
      .query()
      .pipe(map((res: HttpResponse<IVacina[]>) => res.body ?? []))
      .pipe(map((vacinas: IVacina[]) => this.vacinaService.addVacinaToCollectionIfMissing(vacinas, this.editForm.get('vacina')!.value)))
      .subscribe((vacinas: IVacina[]) => (this.vacinasSharedCollection = vacinas));

    this.pessoaService
      .query()
      .pipe(map((res: HttpResponse<IPessoa[]>) => res.body ?? []))
      .pipe(map((pessoas: IPessoa[]) => this.pessoaService.addPessoaToCollectionIfMissing(pessoas, this.editForm.get('pessoa')!.value)))
      .subscribe((pessoas: IPessoa[]) => (this.pessoasSharedCollection = pessoas));
  }

  protected createFromForm(): IToma {
    return {
      ...new Toma(),
      id: this.editForm.get(['id'])!.value,
      data: this.editForm.get(['data'])!.value,
      lote: this.editForm.get(['lote'])!.value,
      dose: this.editForm.get(['dose'])!.value,
      vacina: this.editForm.get(['vacina'])!.value,
      pessoa: this.editForm.get(['pessoa'])!.value,
    };
  }
}
