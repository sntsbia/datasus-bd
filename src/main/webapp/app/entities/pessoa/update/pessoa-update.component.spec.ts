import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PessoaService } from '../service/pessoa.service';
import { IPessoa, Pessoa } from '../pessoa.model';
import { ICondicao } from 'app/entities/condicao/condicao.model';
import { CondicaoService } from 'app/entities/condicao/service/condicao.service';

import { PessoaUpdateComponent } from './pessoa-update.component';

describe('Pessoa Management Update Component', () => {
  let comp: PessoaUpdateComponent;
  let fixture: ComponentFixture<PessoaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let pessoaService: PessoaService;
  let condicaoService: CondicaoService;

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
    condicaoService = TestBed.inject(CondicaoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Condicao query and add missing value', () => {
      const pessoa: IPessoa = { idPessoa: 456 };
      const condicao: ICondicao = { idCondicao: 22102 };
      pessoa.condicao = condicao;

      const condicaoCollection: ICondicao[] = [{ idCondicao: 53223 }];
      jest.spyOn(condicaoService, 'query').mockReturnValue(of(new HttpResponse({ body: condicaoCollection })));
      const additionalCondicaos = [condicao];
      const expectedCollection: ICondicao[] = [...additionalCondicaos, ...condicaoCollection];
      jest.spyOn(condicaoService, 'addCondicaoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ pessoa });
      comp.ngOnInit();

      expect(condicaoService.query).toHaveBeenCalled();
      expect(condicaoService.addCondicaoToCollectionIfMissing).toHaveBeenCalledWith(condicaoCollection, ...additionalCondicaos);
      expect(comp.condicaosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const pessoa: IPessoa = { idPessoa: 456 };
      const condicao: ICondicao = { idCondicao: 32011 };
      pessoa.condicao = condicao;

      activatedRoute.data = of({ pessoa });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(pessoa));
      expect(comp.condicaosSharedCollection).toContain(condicao);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Pessoa>>();
      const pessoa = { idPessoa: 123 };
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
      const pessoa = { idPessoa: 123 };
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
    describe('trackCondicaoByIdCondicao', () => {
      it('Should return tracked Condicao primary key', () => {
        const entity = { idCondicao: 123 };
        const trackResult = comp.trackCondicaoByIdCondicao(0, entity);
        expect(trackResult).toEqual(entity.idCondicao);
      });
    });
  });
});
