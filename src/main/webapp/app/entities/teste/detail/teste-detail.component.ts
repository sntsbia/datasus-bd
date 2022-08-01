import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITeste } from '../teste.model';

@Component({
  selector: 'jhi-teste-detail',
  templateUrl: './teste-detail.component.html',
})
export class TesteDetailComponent implements OnInit {
  teste: ITeste | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ teste }) => {
      this.teste = teste;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
