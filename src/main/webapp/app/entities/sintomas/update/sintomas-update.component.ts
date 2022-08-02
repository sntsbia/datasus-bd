import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISintomas, Sintomas } from '../sintomas.model';
import { SintomasService } from '../service/sintomas.service';

@Component({
  selector: 'jhi-sintomas-update',
  templateUrl: './sintomas-update.component.html',
})
export class SintomasUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    idSintomas: [],
    sintomas: [],
  });

  constructor(protected sintomasService: SintomasService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sintomas }) => {
      this.updateForm(sintomas);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sintomas = this.createFromForm();
    if (sintomas.idSintomas !== undefined) {
      this.subscribeToSaveResponse(this.sintomasService.update(sintomas));
    } else {
      this.subscribeToSaveResponse(this.sintomasService.create(sintomas));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISintomas>>): void {
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

  protected updateForm(sintomas: ISintomas): void {
    this.editForm.patchValue({
      idSintomas: sintomas.idSintomas,
      sintomas: sintomas.sintomas,
    });
  }

  protected createFromForm(): ISintomas {
    return {
      ...new Sintomas(),
      idSintomas: this.editForm.get(['idSintomas'])!.value,
      sintomas: this.editForm.get(['sintomas'])!.value,
    };
  }
}
