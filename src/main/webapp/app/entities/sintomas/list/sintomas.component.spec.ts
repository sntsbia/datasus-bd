import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SintomasService } from '../service/sintomas.service';

import { SintomasComponent } from './sintomas.component';

describe('Sintomas Management Component', () => {
  let comp: SintomasComponent;
  let fixture: ComponentFixture<SintomasComponent>;
  let service: SintomasService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [SintomasComponent],
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
    expect(comp.sintomas?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
