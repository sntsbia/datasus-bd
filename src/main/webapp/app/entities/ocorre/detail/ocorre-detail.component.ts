import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOcorre } from '../ocorre.model';

@Component({
  selector: 'jhi-ocorre-detail',
  templateUrl: './ocorre-detail.component.html',
})
export class OcorreDetailComponent implements OnInit {
  ocorre: IOcorre | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ocorre }) => {
      this.ocorre = ocorre;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
