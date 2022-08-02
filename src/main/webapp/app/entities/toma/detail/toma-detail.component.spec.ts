import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TomaDetailComponent } from './toma-detail.component';

describe('Toma Management Detail Component', () => {
  let comp: TomaDetailComponent;
  let fixture: ComponentFixture<TomaDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TomaDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ toma: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TomaDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TomaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load toma on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.toma).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
