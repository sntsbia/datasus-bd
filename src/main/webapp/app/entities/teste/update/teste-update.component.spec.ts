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
import { IMunicipio } from 'app/entities/municipio/municipio.model';
import { MunicipioService } from 'app/entities/municipio/service/municipio.service';
import { ISintomas } from 'app/entities/sintomas/sintomas.model';
import { SintomasService } from 'app/entities/sintomas/service/sintomas.service';

import { TesteUpdateComponent } from './teste-update.component';

describe('Teste Management Update Component', () => {
  let comp: TesteUpdateComponent;
  let fixture: ComponentFixture<TesteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let testeService: TesteService;
  let pessoaService: PessoaService;
  let municipioService: MunicipioService;
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
    municipioService = TestBed.inject(MunicipioService);
    sintomasService = TestBed.inject(SintomasService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Pessoa query and add missing value', () => {
      const teste: ITeste = { id: 456 };
      const pessoa: IPessoa = { id: 83560 };
      teste.pessoa = pessoa;

      const pessoaCollection: IPessoa[] = [{ id: 39573 }];
      jest.spyOn(pessoaService, 'query').mockReturnValue(of(new HttpResponse({ body: pessoaCollection })));
      const additionalPessoas = [pessoa];
      const expectedCollection: IPessoa[] = [...additionalPessoas, ...pessoaCollection];
      jest.spyOn(pessoaService, 'addPessoaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      expect(pessoaService.query).toHaveBeenCalled();
      expect(pessoaService.addPessoaToCollectionIfMissing).toHaveBeenCalledWith(pessoaCollection, ...additionalPessoas);
      expect(comp.pessoasSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Municipio query and add missing value', () => {
      const teste: ITeste = { id: 456 };
      const municipio: IMunicipio = { id: 84563 };
      teste.municipio = municipio;

      const municipioCollection: IMunicipio[] = [{ id: 63434 }];
      jest.spyOn(municipioService, 'query').mockReturnValue(of(new HttpResponse({ body: municipioCollection })));
      const additionalMunicipios = [municipio];
      const expectedCollection: IMunicipio[] = [...additionalMunicipios, ...municipioCollection];
      jest.spyOn(municipioService, 'addMunicipioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      expect(municipioService.query).toHaveBeenCalled();
      expect(municipioService.addMunicipioToCollectionIfMissing).toHaveBeenCalledWith(municipioCollection, ...additionalMunicipios);
      expect(comp.municipiosSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Sintomas query and add missing value', () => {
      const teste: ITeste = { id: 456 };
      const sintomas: ISintomas[] = [{ id: 52549 }];
      teste.sintomas = sintomas;

      const sintomasCollection: ISintomas[] = [{ id: 49006 }];
      jest.spyOn(sintomasService, 'query').mockReturnValue(of(new HttpResponse({ body: sintomasCollection })));
      const additionalSintomas = [...sintomas];
      const expectedCollection: ISintomas[] = [...additionalSintomas, ...sintomasCollection];
      jest.spyOn(sintomasService, 'addSintomasToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      expect(sintomasService.query).toHaveBeenCalled();
      expect(sintomasService.addSintomasToCollectionIfMissing).toHaveBeenCalledWith(sintomasCollection, ...additionalSintomas);
      expect(comp.sintomasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teste: ITeste = { id: 456 };
      const pessoa: IPessoa = { id: 79285 };
      teste.pessoa = pessoa;
      const municipio: IMunicipio = { id: 1168 };
      teste.municipio = municipio;
      const sintomas: ISintomas = { id: 23187 };
      teste.sintomas = [sintomas];

      activatedRoute.data = of({ teste });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(teste));
      expect(comp.pessoasSharedCollection).toContain(pessoa);
      expect(comp.municipiosSharedCollection).toContain(municipio);
      expect(comp.sintomasSharedCollection).toContain(sintomas);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Teste>>();
      const teste = { id: 123 };
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
      const teste = { id: 123 };
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
    describe('trackPessoaById', () => {
      it('Should return tracked Pessoa primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPessoaById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackMunicipioById', () => {
      it('Should return tracked Municipio primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackMunicipioById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackSintomasById', () => {
      it('Should return tracked Sintomas primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackSintomasById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedSintomas', () => {
      it('Should return option if no Sintomas is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedSintomas(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Sintomas for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedSintomas(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Sintomas is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedSintomas(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
