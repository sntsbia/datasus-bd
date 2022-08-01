import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MunicipioDetailComponent } from './municipio-detail.component';

describe('Municipio Management Detail Component', () => {
  let comp: MunicipioDetailComponent;
  let fixture: ComponentFixture<MunicipioDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MunicipioDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ municipio: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MunicipioDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MunicipioDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load municipio on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.municipio).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
