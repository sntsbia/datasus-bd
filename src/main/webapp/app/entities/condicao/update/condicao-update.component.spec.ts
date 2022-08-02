import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CondicaoService } from '../service/condicao.service';
import { ICondicao, Condicao } from '../condicao.model';

import { CondicaoUpdateComponent } from './condicao-update.component';

describe('Condicao Management Update Component', () => {
  let comp: CondicaoUpdateComponent;
  let fixture: ComponentFixture<CondicaoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let condicaoService: CondicaoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CondicaoUpdateComponent],
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
      .overrideTemplate(CondicaoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CondicaoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    condicaoService = TestBed.inject(CondicaoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const condicao: ICondicao = { idCondicao: 456 };

      activatedRoute.data = of({ condicao });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(condicao));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Condicao>>();
      const condicao = { idCondicao: 123 };
      jest.spyOn(condicaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ condicao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: condicao }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(condicaoService.update).toHaveBeenCalledWith(condicao);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Condicao>>();
      const condicao = new Condicao();
      jest.spyOn(condicaoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ condicao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: condicao }));
      saveSubject.complete();

      // THEN
      expect(condicaoService.create).toHaveBeenCalledWith(condicao);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Condicao>>();
      const condicao = { idCondicao: 123 };
      jest.spyOn(condicaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ condicao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(condicaoService.update).toHaveBeenCalledWith(condicao);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
