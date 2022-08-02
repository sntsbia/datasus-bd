import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MunicipioService } from '../service/municipio.service';

import { MunicipioComponent } from './municipio.component';

describe('Municipio Management Component', () => {
  let comp: MunicipioComponent;
  let fixture: ComponentFixture<MunicipioComponent>;
  let service: MunicipioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'municipio', component: MunicipioComponent }]), HttpClientTestingModule],
      declarations: [MunicipioComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'idMunicipio,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'idMunicipio,desc',
              })
            ),
          },
        },
      ],
    })
      .overrideTemplate(MunicipioComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MunicipioComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MunicipioService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ idMunicipio: 123 }],
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
    expect(comp.municipios?.[0]).toEqual(expect.objectContaining({ idMunicipio: 123 }));
  });

  it('should load a page', () => {
    // WHEN
    comp.loadPage(1);

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.municipios?.[0]).toEqual(expect.objectContaining({ idMunicipio: 123 }));
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalledWith(expect.objectContaining({ sort: ['idMunicipio,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // INIT
    comp.ngOnInit();

    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.loadPage(1);

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['name,desc', 'idMunicipio'] }));
  });
});
