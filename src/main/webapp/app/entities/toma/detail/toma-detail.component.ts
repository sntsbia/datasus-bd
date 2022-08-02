import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IToma } from '../toma.model';

@Component({
  selector: 'jhi-toma-detail',
  templateUrl: './toma-detail.component.html',
})
export class TomaDetailComponent implements OnInit {
  toma: IToma | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ toma }) => {
      this.toma = toma;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
