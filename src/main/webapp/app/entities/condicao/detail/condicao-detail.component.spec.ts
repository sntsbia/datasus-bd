import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CondicaoDetailComponent } from './condicao-detail.component';

describe('Condicao Management Detail Component', () => {
  let comp: CondicaoDetailComponent;
  let fixture: ComponentFixture<CondicaoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CondicaoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ condicao: { idCondicao: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CondicaoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CondicaoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load condicao on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.condicao).toEqual(expect.objectContaining({ idCondicao: 123 }));
    });
  });
});
