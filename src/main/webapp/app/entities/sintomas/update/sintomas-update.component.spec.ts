import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SintomasService } from '../service/sintomas.service';
import { ISintomas, Sintomas } from '../sintomas.model';

import { SintomasUpdateComponent } from './sintomas-update.component';

describe('Sintomas Management Update Component', () => {
  let comp: SintomasUpdateComponent;
  let fixture: ComponentFixture<SintomasUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sintomasService: SintomasService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SintomasUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SintomasUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SintomasUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sintomasService = TestBed.inject(SintomasService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const sintomas: ISintomas = { idSintomas: 456 };

      activatedRoute.data = of({ sintomas });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(sintomas));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Sintomas>>();
      const sintomas = { idSintomas: 123 };
      jest.spyOn(sintomasService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sintomas });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sintomas }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(sintomasService.update).toHaveBeenCalledWith(sintomas);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Sintomas>>();
      const sintomas = new Sintomas();
      jest.spyOn(sintomasService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sintomas });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sintomas }));
      saveSubject.complete();

      // THEN
      expect(sintomasService.create).toHaveBeenCalledWith(sintomas);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Sintomas>>();
      const sintomas = { idSintomas: 123 };
      jest.spyOn(sintomasService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sintomas });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sintomasService.update).toHaveBeenCalledWith(sintomas);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
