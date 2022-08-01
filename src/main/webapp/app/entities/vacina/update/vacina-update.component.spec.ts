import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { VacinaService } from '../service/vacina.service';
import { IVacina, Vacina } from '../vacina.model';

import { VacinaUpdateComponent } from './vacina-update.component';

describe('Vacina Management Update Component', () => {
  let comp: VacinaUpdateComponent;
  let fixture: ComponentFixture<VacinaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let vacinaService: VacinaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [VacinaUpdateComponent],
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
      .overrideTemplate(VacinaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VacinaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    vacinaService = TestBed.inject(VacinaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const vacina: IVacina = { id: 456 };

      activatedRoute.data = of({ vacina });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(vacina));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Vacina>>();
      const vacina = { id: 123 };
      jest.spyOn(vacinaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vacina });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vacina }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(vacinaService.update).toHaveBeenCalledWith(vacina);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Vacina>>();
      const vacina = new Vacina();
      jest.spyOn(vacinaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vacina });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vacina }));
      saveSubject.complete();

      // THEN
      expect(vacinaService.create).toHaveBeenCalledWith(vacina);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Vacina>>();
      const vacina = { id: 123 };
      jest.spyOn(vacinaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vacina });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(vacinaService.update).toHaveBeenCalledWith(vacina);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
