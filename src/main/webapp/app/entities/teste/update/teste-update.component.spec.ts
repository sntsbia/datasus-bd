import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TesteService } from '../service/teste.service';
import { ITeste, Teste } from '../teste.model';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';
import { ISintomas } from 'app/entities/sintomas/sintomas.model';
import { SintomasService } from 'app/entities/sintomas/service/sintomas.service';

import { TesteUpdateComponent } from './teste-update.component';

describe('Teste Management Update Component', () => {
  let comp: TesteUpdateComponent;
  let fixture: ComponentFixture<TesteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let testeService: TesteService;
  let pessoaService: PessoaService;
  let sintomasService: SintomasService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TesteUpdateComponent],
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
      .overrideTemplate(TesteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TesteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    testeService = TestBed.inject(TesteService);
    pessoaService = TestBed.inject(PessoaService);
    sintomasService = TestBed.inject(SintomasService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Pessoa query and add missing value', () => {
      const teste: ITeste = { idTeste: 456 };
      const fkIdTeste: IPessoa = { idPessoa: 83560 };
      teste.fkIdTeste = fkIdTeste;

      const pessoaCollection: IPessoa[] = [{ idPessoa: 39573 }];
      jest.spyOn(pessoaService, 'query').mockReturnValue(of(new HttpResponse({ body: pessoaCollection })));
      const additionalPessoas = [fkIdTeste];
      const expectedCollection: IPessoa[] = [...additionalPessoas, ...pessoaCollection];
      jest.spyOn(pessoaService, 'addPessoaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      expect(pessoaService.query).toHaveBeenCalled();
      expect(pessoaService.addPessoaToCollectionIfMissing).toHaveBeenCalledWith(pessoaCollection, ...additionalPessoas);
      expect(comp.pessoasSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Sintomas query and add missing value', () => {
      const teste: ITeste = { idTeste: 456 };
      const sintomas: ISintomas = { idSintomas: 52549 };
      teste.sintomas = sintomas;

      const sintomasCollection: ISintomas[] = [{ idSintomas: 49006 }];
      jest.spyOn(sintomasService, 'query').mockReturnValue(of(new HttpResponse({ body: sintomasCollection })));
      const additionalSintomas = [sintomas];
      const expectedCollection: ISintomas[] = [...additionalSintomas, ...sintomasCollection];
      jest.spyOn(sintomasService, 'addSintomasToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      expect(sintomasService.query).toHaveBeenCalled();
      expect(sintomasService.addSintomasToCollectionIfMissing).toHaveBeenCalledWith(sintomasCollection, ...additionalSintomas);
      expect(comp.sintomasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teste: ITeste = { idTeste: 456 };
      const fkIdTeste: IPessoa = { idPessoa: 79285 };
      teste.fkIdTeste = fkIdTeste;
      const sintomas: ISintomas = { idSintomas: 23187 };
      teste.sintomas = sintomas;

      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(teste));
      expect(comp.pessoasSharedCollection).toContain(fkIdTeste);
      expect(comp.sintomasSharedCollection).toContain(sintomas);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Teste>>();
      const teste = { idTeste: 123 };
      jest.spyOn(testeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teste }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(testeService.update).toHaveBeenCalledWith(teste);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Teste>>();
      const teste = new Teste();
      jest.spyOn(testeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teste }));
      saveSubject.complete();

      // THEN
      expect(testeService.create).toHaveBeenCalledWith(teste);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Teste>>();
      const teste = { idTeste: 123 };
      jest.spyOn(testeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(testeService.update).toHaveBeenCalledWith(teste);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPessoaByIdPessoa', () => {
      it('Should return tracked Pessoa primary key', () => {
        const entity = { idPessoa: 123 };
        const trackResult = comp.trackPessoaByIdPessoa(0, entity);
        expect(trackResult).toEqual(entity.idPessoa);
      });
    });

    describe('trackSintomasByIdSintomas', () => {
      it('Should return tracked Sintomas primary key', () => {
        const entity = { idSintomas: 123 };
        const trackResult = comp.trackSintomasByIdSintomas(0, entity);
        expect(trackResult).toEqual(entity.idSintomas);
      });
    });
  });
});
