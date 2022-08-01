import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OcorreDetailComponent } from './ocorre-detail.component';

describe('Ocorre Management Detail Component', () => {
  let comp: OcorreDetailComponent;
  let fixture: ComponentFixture<OcorreDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OcorreDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ocorre: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OcorreDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OcorreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ocorre on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ocorre).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
