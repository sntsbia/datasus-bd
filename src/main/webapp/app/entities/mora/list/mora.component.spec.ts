import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { MoraService } from '../service/mora.service';

import { MoraComponent } from './mora.component';

describe('Mora Management Component', () => {
  let comp: MoraComponent;
  let fixture: ComponentFixture<MoraComponent>;
  let service: MoraService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [MoraComponent],
    })
      .overrideTemplate(MoraComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MoraComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MoraService);

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
    expect(comp.moras?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
