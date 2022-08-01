import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { UfService } from '../service/uf.service';

import { UfComponent } from './uf.component';

describe('Uf Management Component', () => {
  let comp: UfComponent;
  let fixture: ComponentFixture<UfComponent>;
  let service: UfService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [UfComponent],
    })
      .overrideTemplate(UfComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UfComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UfService);

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
    expect(comp.ufs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
