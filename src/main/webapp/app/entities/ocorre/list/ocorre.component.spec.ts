import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { OcorreService } from '../service/ocorre.service';

import { OcorreComponent } from './ocorre.component';

describe('Ocorre Management Component', () => {
  let comp: OcorreComponent;
  let fixture: ComponentFixture<OcorreComponent>;
  let service: OcorreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [OcorreComponent],
    })
      .overrideTemplate(OcorreComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OcorreComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OcorreService);

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
    expect(comp.ocorres?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
