import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TesteDetailComponent } from './teste-detail.component';

describe('Teste Management Detail Component', () => {
  let comp: TesteDetailComponent;
  let fixture: ComponentFixture<TesteDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TesteDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ teste: { idTeste: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TesteDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TesteDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load teste on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.teste).toEqual(expect.objectContaining({ idTeste: 123 }));
    });
  });
});
