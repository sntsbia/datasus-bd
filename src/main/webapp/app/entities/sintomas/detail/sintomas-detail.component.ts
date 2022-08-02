import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISintomas } from '../sintomas.model';

@Component({
  selector: 'jhi-sintomas-detail',
  templateUrl: './sintomas-detail.component.html',
})
export class SintomasDetailComponent implements OnInit {
  sintomas: ISintomas | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sintomas }) => {
      this.sintomas = sintomas;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
