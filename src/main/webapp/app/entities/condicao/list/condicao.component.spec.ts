import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CondicaoService } from '../service/condicao.service';

import { CondicaoComponent } from './condicao.component';

describe('Condicao Management Component', () => {
  let comp: CondicaoComponent;
  let fixture: ComponentFixture<CondicaoComponent>;
  let service: CondicaoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'condicao', component: CondicaoComponent }]), HttpClientTestingModule],
      declarations: [CondicaoComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'idCondicao,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'idCondicao,desc',
              })
            ),
          },
        },
      ],
    })
      .overrideTemplate(CondicaoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CondicaoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CondicaoService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ idCondicao: 123 }],
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
    expect(comp.condicaos?.[0]).toEqual(expect.objectContaining({ idCondicao: 123 }));
  });

  it('should load a page', () => {
    // WHEN
    comp.loadPage(1);

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.condicaos?.[0]).toEqual(expect.objectContaining({ idCondicao: 123 }));
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalledWith(expect.objectContaining({ sort: ['idCondicao,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // INIT
    comp.ngOnInit();

    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.loadPage(1);

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['name,desc', 'idCondicao'] }));
  });
});
