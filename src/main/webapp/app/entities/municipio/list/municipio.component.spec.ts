import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { MunicipioService } from '../service/municipio.service';

import { MunicipioComponent } from './municipio.component';

describe('Municipio Management Component', () => {
  let comp: MunicipioComponent;
  let fixture: ComponentFixture<MunicipioComponent>;
  let service: MunicipioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [MunicipioComponent],
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
    expect(comp.municipios?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
