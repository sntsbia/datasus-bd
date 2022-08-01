import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CondicoesService } from '../service/condicoes.service';

import { CondicoesComponent } from './condicoes.component';

describe('Condicoes Management Component', () => {
  let comp: CondicoesComponent;
  let fixture: ComponentFixture<CondicoesComponent>;
  let service: CondicoesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [CondicoesComponent],
    })
      .overrideTemplate(CondicoesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CondicoesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CondicoesService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.condicoes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
