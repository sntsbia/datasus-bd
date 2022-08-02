import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMora } from '../mora.model';

@Component({
  selector: 'jhi-mora-detail',
  templateUrl: './mora-detail.component.html',
})
export class MoraDetailComponent implements OnInit {
  mora: IMora | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mora }) => {
      this.mora = mora;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
