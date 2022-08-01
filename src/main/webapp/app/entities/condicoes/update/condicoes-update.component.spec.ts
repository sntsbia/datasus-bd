import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CondicoesService } from '../service/condicoes.service';
import { ICondicoes, Condicoes } from '../condicoes.model';

import { CondicoesUpdateComponent } from './condicoes-update.component';

describe('Condicoes Management Update Component', () => {
  let comp: CondicoesUpdateComponent;
  let fixture: ComponentFixture<CondicoesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let condicoesService: CondicoesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CondicoesUpdateComponent],
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
      .overrideTemplate(CondicoesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CondicoesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    condicoesService = TestBed.inject(CondicoesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const condicoes: ICondicoes = { id: 456 };

      activatedRoute.data = of({ condicoes });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(condicoes));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Condicoes>>();
      const condicoes = { id: 123 };
      jest.spyOn(condicoesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ condicoes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: condicoes }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(condicoesService.update).toHaveBeenCalledWith(condicoes);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Condicoes>>();
      const condicoes = new Condicoes();
      jest.spyOn(condicoesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ condicoes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: condicoes }));
      saveSubject.complete();

      // THEN
      expect(condicoesService.create).toHaveBeenCalledWith(condicoes);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Condicoes>>();
      const condicoes = { id: 123 };
      jest.spyOn(condicoesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ condicoes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(condicoesService.update).toHaveBeenCalledWith(condicoes);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
