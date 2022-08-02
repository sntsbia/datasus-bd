import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMunicipio } from '../municipio.model';

@Component({
  selector: 'jhi-municipio-detail',
  templateUrl: './municipio-detail.component.html',
})
export class MunicipioDetailComponent implements OnInit {
  municipio: IMunicipio | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ municipio }) => {
      this.municipio = municipio;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
