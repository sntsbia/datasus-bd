import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OcorreService } from '../service/ocorre.service';
import { IOcorre, Ocorre } from '../ocorre.model';
import { ITeste } from 'app/entities/teste/teste.model';
import { TesteService } from 'app/entities/teste/service/teste.service';
import { IMunicipio } from 'app/entities/municipio/municipio.model';
import { MunicipioService } from 'app/entities/municipio/service/municipio.service';

import { OcorreUpdateComponent } from './ocorre-update.component';

describe('Ocorre Management Update Component', () => {
  let comp: OcorreUpdateComponent;
  let fixture: ComponentFixture<OcorreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ocorreService: OcorreService;
  let testeService: TesteService;
  let municipioService: MunicipioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OcorreUpdateComponent],
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
      .overrideTemplate(OcorreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OcorreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ocorreService = TestBed.inject(OcorreService);
    testeService = TestBed.inject(TesteService);
    municipioService = TestBed.inject(MunicipioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Teste query and add missing value', () => {
      const ocorre: IOcorre = { id: 456 };
      const fkIdTeste: ITeste = { idTeste: 93916 };
      ocorre.fkIdTeste = fkIdTeste;

      const testeCollection: ITeste[] = [{ idTeste: 11839 }];
      jest.spyOn(testeService, 'query').mockReturnValue(of(new HttpResponse({ body: testeCollection })));
      const additionalTestes = [fkIdTeste];
      const expectedCollection: ITeste[] = [...additionalTestes, ...testeCollection];
      jest.spyOn(testeService, 'addTesteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ocorre });
      comp.ngOnInit();

      expect(testeService.query).toHaveBeenCalled();
      expect(testeService.addTesteToCollectionIfMissing).toHaveBeenCalledWith(testeCollection, ...additionalTestes);
      expect(comp.testesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Municipio query and add missing value', () => {
      const ocorre: IOcorre = { id: 456 };
      const fkIdMunicipio: IMunicipio = { idMunicipio: 97663 };
      ocorre.fkIdMunicipio = fkIdMunicipio;

      const municipioCollection: IMunicipio[] = [{ idMunicipio: 53510 }];
      jest.spyOn(municipioService, 'query').mockReturnValue(of(new HttpResponse({ body: municipioCollection })));
      const additionalMunicipios = [fkIdMunicipio];
      const expectedCollection: IMunicipio[] = [...additionalMunicipios, ...municipioCollection];
      jest.spyOn(municipioService, 'addMunicipioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ocorre });
      comp.ngOnInit();

      expect(municipioService.query).toHaveBeenCalled();
      expect(municipioService.addMunicipioToCollectionIfMissing).toHaveBeenCalledWith(municipioCollection, ...additionalMunicipios);
      expect(comp.municipiosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ocorre: IOcorre = { id: 456 };
      const fkIdTeste: ITeste = { idTeste: 34802 };
      ocorre.fkIdTeste = fkIdTeste;
      const fkIdMunicipio: IMunicipio = { idMunicipio: 49749 };
      ocorre.fkIdMunicipio = fkIdMunicipio;

      activatedRoute.data = of({ ocorre });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ocorre));
      expect(comp.testesSharedCollection).toContain(fkIdTeste);
      expect(comp.municipiosSharedCollection).toContain(fkIdMunicipio);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ocorre>>();
      const ocorre = { id: 123 };
      jest.spyOn(ocorreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ocorre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ocorre }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(ocorreService.update).toHaveBeenCalledWith(ocorre);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ocorre>>();
      const ocorre = new Ocorre();
      jest.spyOn(ocorreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ocorre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ocorre }));
      saveSubject.complete();

      // THEN
      expect(ocorreService.create).toHaveBeenCalledWith(ocorre);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ocorre>>();
      const ocorre = { id: 123 };
      jest.spyOn(ocorreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ocorre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ocorreService.update).toHaveBeenCalledWith(ocorre);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTesteByIdTeste', () => {
      it('Should return tracked Teste primary key', () => {
        const entity = { idTeste: 123 };
        const trackResult = comp.trackTesteByIdTeste(0, entity);
        expect(trackResult).toEqual(entity.idTeste);
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
