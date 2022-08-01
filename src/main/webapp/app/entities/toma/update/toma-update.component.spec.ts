import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TomaService } from '../service/toma.service';
import { IToma, Toma } from '../toma.model';
import { IVacina } from 'app/entities/vacina/vacina.model';
import { VacinaService } from 'app/entities/vacina/service/vacina.service';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';

import { TomaUpdateComponent } from './toma-update.component';

describe('Toma Management Update Component', () => {
  let comp: TomaUpdateComponent;
  let fixture: ComponentFixture<TomaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tomaService: TomaService;
  let vacinaService: VacinaService;
  let pessoaService: PessoaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TomaUpdateComponent],
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
      .overrideTemplate(TomaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TomaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tomaService = TestBed.inject(TomaService);
    vacinaService = TestBed.inject(VacinaService);
    pessoaService = TestBed.inject(PessoaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Vacina query and add missing value', () => {
      const toma: IToma = { id: 456 };
      const vacina: IVacina = { id: 11744 };
      toma.vacina = vacina;

      const vacinaCollection: IVacina[] = [{ id: 76270 }];
      jest.spyOn(vacinaService, 'query').mockReturnValue(of(new HttpResponse({ body: vacinaCollection })));
      const additionalVacinas = [vacina];
      const expectedCollection: IVacina[] = [...additionalVacinas, ...vacinaCollection];
      jest.spyOn(vacinaService, 'addVacinaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ toma });
      comp.ngOnInit();

      expect(vacinaService.query).toHaveBeenCalled();
      expect(vacinaService.addVacinaToCollectionIfMissing).toHaveBeenCalledWith(vacinaCollection, ...additionalVacinas);
      expect(comp.vacinasSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Pessoa query and add missing value', () => {
      const toma: IToma = { id: 456 };
      const pessoa: IPessoa = { id: 22458 };
      toma.pessoa = pessoa;

      const pessoaCollection: IPessoa[] = [{ id: 83735 }];
      jest.spyOn(pessoaService, 'query').mockReturnValue(of(new HttpResponse({ body: pessoaCollection })));
      const additionalPessoas = [pessoa];
      const expectedCollection: IPessoa[] = [...additionalPessoas, ...pessoaCollection];
      jest.spyOn(pessoaService, 'addPessoaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ toma });
      comp.ngOnInit();

      expect(pessoaService.query).toHaveBeenCalled();
      expect(pessoaService.addPessoaToCollectionIfMissing).toHaveBeenCalledWith(pessoaCollection, ...additionalPessoas);
      expect(comp.pessoasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const toma: IToma = { id: 456 };
      const vacina: IVacina = { id: 83875 };
      toma.vacina = vacina;
      const pessoa: IPessoa = { id: 79387 };
      toma.pessoa = pessoa;

      activatedRoute.data = of({ toma });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(toma));
      expect(comp.vacinasSharedCollection).toContain(vacina);
      expect(comp.pessoasSharedCollection).toContain(pessoa);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Toma>>();
      const toma = { id: 123 };
      jest.spyOn(tomaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ toma });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: toma }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tomaService.update).toHaveBeenCalledWith(toma);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Toma>>();
      const toma = new Toma();
      jest.spyOn(tomaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ toma });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: toma }));
      saveSubject.complete();

      // THEN
      expect(tomaService.create).toHaveBeenCalledWith(toma);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Toma>>();
      const toma = { id: 123 };
      jest.spyOn(tomaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ toma });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tomaService.update).toHaveBeenCalledWith(toma);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackVacinaById', () => {
      it('Should return tracked Vacina primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackVacinaById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPessoaById', () => {
      it('Should return tracked Pessoa primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPessoaById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
