import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UfService } from '../service/uf.service';
import { IUf, Uf } from '../uf.model';

import { UfUpdateComponent } from './uf-update.component';

describe('Uf Management Update Component', () => {
  let comp: UfUpdateComponent;
  let fixture: ComponentFixture<UfUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ufService: UfService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UfUpdateComponent],
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
      .overrideTemplate(UfUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UfUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ufService = TestBed.inject(UfService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const uf: IUf = { idUf: 456 };

      activatedRoute.data = of({ uf });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(uf));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Uf>>();
      const uf = { idUf: 123 };
      jest.spyOn(ufService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uf });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: uf }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(ufService.update).toHaveBeenCalledWith(uf);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Uf>>();
      const uf = new Uf();
      jest.spyOn(ufService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uf });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: uf }));
      saveSubject.complete();

      // THEN
      expect(ufService.create).toHaveBeenCalledWith(uf);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Uf>>();
      const uf = { idUf: 123 };
      jest.spyOn(ufService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ uf });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ufService.update).toHaveBeenCalledWith(uf);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
