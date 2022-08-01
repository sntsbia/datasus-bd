import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PessoaService } from '../service/pessoa.service';
import { IPessoa, Pessoa } from '../pessoa.model';
import { ICondicoes } from 'app/entities/condicoes/condicoes.model';
import { CondicoesService } from 'app/entities/condicoes/service/condicoes.service';

import { PessoaUpdateComponent } from './pessoa-update.component';

describe('Pessoa Management Update Component', () => {
  let comp: PessoaUpdateComponent;
  let fixture: ComponentFixture<PessoaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let pessoaService: PessoaService;
  let condicoesService: CondicoesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PessoaUpdateComponent],
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
      .overrideTemplate(PessoaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PessoaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    pessoaService = TestBed.inject(PessoaService);
    condicoesService = TestBed.inject(CondicoesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Condicoes query and add missing value', () => {
      const pessoa: IPessoa = { id: 456 };
      const condicoes: ICondicoes[] = [{ id: 1201 }];
      pessoa.condicoes = condicoes;

      const condicoesCollection: ICondicoes[] = [{ id: 14669 }];
      jest.spyOn(condicoesService, 'query').mockReturnValue(of(new HttpResponse({ body: condicoesCollection })));
      const additionalCondicoes = [...condicoes];
      const expectedCollection: ICondicoes[] = [...additionalCondicoes, ...condicoesCollection];
      jest.spyOn(condicoesService, 'addCondicoesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pessoa });
      comp.ngOnInit();

      expect(condicoesService.query).toHaveBeenCalled();
      expect(condicoesService.addCondicoesToCollectionIfMissing).toHaveBeenCalledWith(condicoesCollection, ...additionalCondicoes);
      expect(comp.condicoesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pessoa: IPessoa = { id: 456 };
      const condicoes: ICondicoes = { id: 64625 };
      pessoa.condicoes = [condicoes];

      activatedRoute.data = of({ pessoa });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(pessoa));
      expect(comp.condicoesSharedCollection).toContain(condicoes);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pessoa>>();
      const pessoa = { id: 123 };
      jest.spyOn(pessoaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pessoa });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pessoa }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(pessoaService.update).toHaveBeenCalledWith(pessoa);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pessoa>>();
      const pessoa = new Pessoa();
      jest.spyOn(pessoaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pessoa });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: pessoa }));
      saveSubject.complete();

      // THEN
      expect(pessoaService.create).toHaveBeenCalledWith(pessoa);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pessoa>>();
      const pessoa = { id: 123 };
      jest.spyOn(pessoaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ pessoa });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(pessoaService.update).toHaveBeenCalledWith(pessoa);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCondicoesById', () => {
      it('Should return tracked Condicoes primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCondicoesById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedCondicoes', () => {
      it('Should return option if no Condicoes is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedCondicoes(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Condicoes for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedCondicoes(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Condicoes is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedCondicoes(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
