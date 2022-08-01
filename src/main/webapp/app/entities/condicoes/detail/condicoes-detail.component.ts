import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICondicoes } from '../condicoes.model';

@Component({
  selector: 'jhi-condicoes-detail',
  templateUrl: './condicoes-detail.component.html',
})
export class CondicoesDetailComponent implements OnInit {
  condicoes: ICondicoes | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ condicoes }) => {
      this.condicoes = condicoes;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
