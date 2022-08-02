import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SintomasService } from '../service/sintomas.service';

import { SintomasComponent } from './sintomas.component';

describe('Sintomas Management Component', () => {
  let comp: SintomasComponent;
  let fixture: ComponentFixture<SintomasComponent>;
  let service: SintomasService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'sintomas', component: SintomasComponent }]), HttpClientTestingModule],
      declarations: [SintomasComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'idSintomas,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'idSintomas,desc',
              })
            ),
          },
        },
      ],
    })
      .overrideTemplate(SintomasComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SintomasComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SintomasService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ idSintomas: 123 }],
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
    expect(comp.sintomas?.[0]).toEqual(expect.objectContaining({ idSintomas: 123 }));
  });

  it('should load a page', () => {
    // WHEN
    comp.loadPage(1);

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.sintomas?.[0]).toEqual(expect.objectContaining({ idSintomas: 123 }));
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalledWith(expect.objectContaining({ sort: ['idSintomas,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // INIT
    comp.ngOnInit();

    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.loadPage(1);

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['name,desc', 'idSintomas'] }));
  });
});
