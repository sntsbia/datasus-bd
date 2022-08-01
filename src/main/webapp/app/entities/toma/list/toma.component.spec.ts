import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TomaService } from '../service/toma.service';

import { TomaComponent } from './toma.component';

describe('Toma Management Component', () => {
  let comp: TomaComponent;
  let fixture: ComponentFixture<TomaComponent>;
  let service: TomaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TomaComponent],
    })
      .overrideTemplate(TomaComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TomaComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TomaService);

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
    expect(comp.tomas?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
