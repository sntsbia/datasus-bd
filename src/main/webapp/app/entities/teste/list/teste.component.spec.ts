import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TesteService } from '../service/teste.service';

import { TesteComponent } from './teste.component';

describe('Teste Management Component', () => {
  let comp: TesteComponent;
  let fixture: ComponentFixture<TesteComponent>;
  let service: TesteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TesteComponent],
    })
      .overrideTemplate(TesteComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TesteComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TesteService);

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
    expect(comp.testes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
