import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUf, Uf } from '../uf.model';
import { UfService } from '../service/uf.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-uf-update',
  templateUrl: './uf-update.component.html',
})
export class UfUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    codigoIbge: [],
    estado: [],
    bandeira: [],
    bandeiraContentType: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected ufService: UfService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ uf }) => {
      this.updateForm(uf);
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('datasusBdApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const uf = this.createFromForm();
    if (uf.id !== undefined) {
      this.subscribeToSaveResponse(this.ufService.update(uf));
    } else {
      this.subscribeToSaveResponse(this.ufService.create(uf));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUf>>): void {
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

  protected updateForm(uf: IUf): void {
    this.editForm.patchValue({
      id: uf.id,
      codigoIbge: uf.codigoIbge,
      estado: uf.estado,
      bandeira: uf.bandeira,
      bandeiraContentType: uf.bandeiraContentType,
    });
  }

  protected createFromForm(): IUf {
    return {
      ...new Uf(),
      id: this.editForm.get(['id'])!.value,
      codigoIbge: this.editForm.get(['codigoIbge'])!.value,
      estado: this.editForm.get(['estado'])!.value,
      bandeiraContentType: this.editForm.get(['bandeiraContentType'])!.value,
      bandeira: this.editForm.get(['bandeira'])!.value,
    };
  }
}
