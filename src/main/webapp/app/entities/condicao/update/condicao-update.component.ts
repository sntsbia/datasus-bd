import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ICondicao, Condicao } from '../condicao.model';
import { CondicaoService } from '../service/condicao.service';

@Component({
  selector: 'jhi-condicao-update',
  templateUrl: './condicao-update.component.html',
})
export class CondicaoUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    idCondicao: [],
    condicao: [],
  });

  constructor(protected condicaoService: CondicaoService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ condicao }) => {
      this.updateForm(condicao);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const condicao = this.createFromForm();
    if (condicao.idCondicao !== undefined) {
      this.subscribeToSaveResponse(this.condicaoService.update(condicao));
    } else {
      this.subscribeToSaveResponse(this.condicaoService.create(condicao));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICondicao>>): void {
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

  protected updateForm(condicao: ICondicao): void {
    this.editForm.patchValue({
      idCondicao: condicao.idCondicao,
      condicao: condicao.condicao,
    });
  }

  protected createFromForm(): ICondicao {
    return {
      ...new Condicao(),
      idCondicao: this.editForm.get(['idCondicao'])!.value,
      condicao: this.editForm.get(['condicao'])!.value,
    };
  }
}
