import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ICondicoes, Condicoes } from '../condicoes.model';
import { CondicoesService } from '../service/condicoes.service';

@Component({
  selector: 'jhi-condicoes-update',
  templateUrl: './condicoes-update.component.html',
})
export class CondicoesUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    condicao: [],
  });

  constructor(protected condicoesService: CondicoesService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ condicoes }) => {
      this.updateForm(condicoes);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const condicoes = this.createFromForm();
    if (condicoes.id !== undefined) {
      this.subscribeToSaveResponse(this.condicoesService.update(condicoes));
    } else {
      this.subscribeToSaveResponse(this.condicoesService.create(condicoes));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICondicoes>>): void {
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

  protected updateForm(condicoes: ICondicoes): void {
    this.editForm.patchValue({
      id: condicoes.id,
      condicao: condicoes.condicao,
    });
  }

  protected createFromForm(): ICondicoes {
    return {
      ...new Condicoes(),
      id: this.editForm.get(['id'])!.value,
      condicao: this.editForm.get(['condicao'])!.value,
    };
  }
}
