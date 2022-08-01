import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { VacinaDetailComponent } from './vacina-detail.component';

describe('Vacina Management Detail Component', () => {
  let comp: VacinaDetailComponent;
  let fixture: ComponentFixture<VacinaDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VacinaDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ vacina: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(VacinaDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(VacinaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load vacina on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.vacina).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
