import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SintomasDetailComponent } from './sintomas-detail.component';

describe('Sintomas Management Detail Component', () => {
  let comp: SintomasDetailComponent;
  let fixture: ComponentFixture<SintomasDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SintomasDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ sintomas: { idSintomas: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SintomasDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SintomasDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load sintomas on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.sintomas).toEqual(expect.objectContaining({ idSintomas: 123 }));
    });
  });
});
