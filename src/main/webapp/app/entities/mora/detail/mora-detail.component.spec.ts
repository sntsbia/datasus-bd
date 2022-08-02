import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MoraDetailComponent } from './mora-detail.component';

describe('Mora Management Detail Component', () => {
  let comp: MoraDetailComponent;
  let fixture: ComponentFixture<MoraDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MoraDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ mora: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MoraDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MoraDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load mora on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.mora).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
