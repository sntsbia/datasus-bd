import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CondicoesDetailComponent } from './condicoes-detail.component';

describe('Condicoes Management Detail Component', () => {
  let comp: CondicoesDetailComponent;
  let fixture: ComponentFixture<CondicoesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CondicoesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ condicoes: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CondicoesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CondicoesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load condicoes on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.condicoes).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
