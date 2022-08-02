import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICondicao } from '../condicao.model';

@Component({
  selector: 'jhi-condicao-detail',
  templateUrl: './condicao-detail.component.html',
})
export class CondicaoDetailComponent implements OnInit {
  condicao: ICondicao | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ condicao }) => {
      this.condicao = condicao;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
