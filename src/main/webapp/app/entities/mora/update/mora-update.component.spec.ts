import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MoraService } from '../service/mora.service';
import { IMora, Mora } from '../mora.model';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { PessoaService } from 'app/entities/pessoa/service/pessoa.service';
import { IMunicipio } from 'app/entities/municipio/municipio.model';
import { MunicipioService } from 'app/entities/municipio/service/municipio.service';

import { MoraUpdateComponent } from './mora-update.component';

describe('Mora Management Update Component', () => {
  let comp: MoraUpdateComponent;
  let fixture: ComponentFixture<MoraUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let moraService: MoraService;
  let pessoaService: PessoaService;
  let municipioService: MunicipioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MoraUpdateComponent],
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
      .overrideTemplate(MoraUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MoraUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    moraService = TestBed.inject(MoraService);
    pessoaService = TestBed.inject(PessoaService);
    municipioService = TestBed.inject(MunicipioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Pessoa query and add missing value', () => {
      const mora: IMora = { id: 456 };
      const fkIdPessoa: IPessoa = { idPessoa: 83935 };
      mora.fkIdPessoa = fkIdPessoa;

      const pessoaCollection: IPessoa[] = [{ idPessoa: 31530 }];
      jest.spyOn(pessoaService, 'query').mockReturnValue(of(new HttpResponse({ body: pessoaCollection })));
      const additionalPessoas = [fkIdPessoa];
      const expectedCollection: IPessoa[] = [...additionalPessoas, ...pessoaCollection];
      jest.spyOn(pessoaService, 'addPessoaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mora });
      comp.ngOnInit();

      expect(pessoaService.query).toHaveBeenCalled();
      expect(pessoaService.addPessoaToCollectionIfMissing).toHaveBeenCalledWith(pessoaCollection, ...additionalPessoas);
      expect(comp.pessoasSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Municipio query and add missing value', () => {
      const mora: IMora = { id: 456 };
      const fkIdMunicipio: IMunicipio = { idMunicipio: 3500 };
      mora.fkIdMunicipio = fkIdMunicipio;

      const municipioCollection: IMunicipio[] = [{ idMunicipio: 37578 }];
      jest.spyOn(municipioService, 'query').mockReturnValue(of(new HttpResponse({ body: municipioCollection })));
      const additionalMunicipios = [fkIdMunicipio];
      const expectedCollection: IMunicipio[] = [...additionalMunicipios, ...municipioCollection];
      jest.spyOn(municipioService, 'addMunicipioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mora });
      comp.ngOnInit();

      expect(municipioService.query).toHaveBeenCalled();
      expect(municipioService.addMunicipioToCollectionIfMissing).toHaveBeenCalledWith(municipioCollection, ...additionalMunicipios);
      expect(comp.municipiosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const mora: IMora = { id: 456 };
      const fkIdPessoa: IPessoa = { idPessoa: 30401 };
      mora.fkIdPessoa = fkIdPessoa;
      const fkIdMunicipio: IMunicipio = { idMunicipio: 87693 };
      mora.fkIdMunicipio = fkIdMunicipio;

      activatedRoute.data = of({ mora });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(mora));
      expect(comp.pessoasSharedCollection).toContain(fkIdPessoa);
      expect(comp.municipiosSharedCollection).toContain(fkIdMunicipio);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mora>>();
      const mora = { id: 123 };
      jest.spyOn(moraService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mora });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mora }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(moraService.update).toHaveBeenCalledWith(mora);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mora>>();
      const mora = new Mora();
      jest.spyOn(moraService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mora });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mora }));
      saveSubject.complete();

      // THEN
      expect(moraService.create).toHaveBeenCalledWith(mora);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mora>>();
      const mora = { id: 123 };
      jest.spyOn(moraService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mora });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(moraService.update).toHaveBeenCalledWith(mora);
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

    describe('trackMunicipioByIdMunicipio', () => {
      it('Should return tracked Municipio primary key', () => {
        const entity = { idMunicipio: 123 };
        const trackResult = comp.trackMunicipioByIdMunicipio(0, entity);
        expect(trackResult).toEqual(entity.idMunicipio);
      });
    });
  });
});
