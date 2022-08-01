import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MunicipioService } from '../service/municipio.service';
import { IMunicipio, Municipio } from '../municipio.model';
import { IUf } from 'app/entities/uf/uf.model';
import { UfService } from 'app/entities/uf/service/uf.service';

import { MunicipioUpdateComponent } from './municipio-update.component';

describe('Municipio Management Update Component', () => {
  let comp: MunicipioUpdateComponent;
  let fixture: ComponentFixture<MunicipioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let municipioService: MunicipioService;
  let ufService: UfService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MunicipioUpdateComponent],
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
      .overrideTemplate(MunicipioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MunicipioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    municipioService = TestBed.inject(MunicipioService);
    ufService = TestBed.inject(UfService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Uf query and add missing value', () => {
      const municipio: IMunicipio = { id: 456 };
      const uf: IUf = { id: 31658 };
      municipio.uf = uf;

      const ufCollection: IUf[] = [{ id: 43119 }];
      jest.spyOn(ufService, 'query').mockReturnValue(of(new HttpResponse({ body: ufCollection })));
      const additionalUfs = [uf];
      const expectedCollection: IUf[] = [...additionalUfs, ...ufCollection];
      jest.spyOn(ufService, 'addUfToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ municipio });
      comp.ngOnInit();

      expect(ufService.query).toHaveBeenCalled();
      expect(ufService.addUfToCollectionIfMissing).toHaveBeenCalledWith(ufCollection, ...additionalUfs);
      expect(comp.ufsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const municipio: IMunicipio = { id: 456 };
      const uf: IUf = { id: 21498 };
      municipio.uf = uf;

      activatedRoute.data = of({ municipio });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(municipio));
      expect(comp.ufsSharedCollection).toContain(uf);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Municipio>>();
      const municipio = { id: 123 };
      jest.spyOn(municipioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ municipio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: municipio }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(municipioService.update).toHaveBeenCalledWith(municipio);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Municipio>>();
      const municipio = new Municipio();
      jest.spyOn(municipioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ municipio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: municipio }));
      saveSubject.complete();

      // THEN
      expect(municipioService.create).toHaveBeenCalledWith(municipio);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Municipio>>();
      const municipio = { id: 123 };
      jest.spyOn(municipioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ municipio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(municipioService.update).toHaveBeenCalledWith(municipio);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUfById', () => {
      it('Should return tracked Uf primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUfById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
