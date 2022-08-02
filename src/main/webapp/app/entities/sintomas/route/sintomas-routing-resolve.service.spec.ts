import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ISintomas, Sintomas } from '../sintomas.model';
import { SintomasService } from '../service/sintomas.service';

import { SintomasRoutingResolveService } from './sintomas-routing-resolve.service';

describe('Sintomas routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: SintomasRoutingResolveService;
  let service: SintomasService;
  let resultSintomas: ISintomas | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(SintomasRoutingResolveService);
    service = TestBed.inject(SintomasService);
    resultSintomas = undefined;
  });

  describe('resolve', () => {
    it('should return ISintomas returned by find', () => {
      // GIVEN
      service.find = jest.fn(idSintomas => of(new HttpResponse({ body: { idSintomas } })));
      mockActivatedRouteSnapshot.params = { idSintomas: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSintomas = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSintomas).toEqual({ idSintomas: 123 });
    });

    it('should return new ISintomas if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSintomas = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultSintomas).toEqual(new Sintomas());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Sintomas })));
      mockActivatedRouteSnapshot.params = { idSintomas: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSintomas = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSintomas).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
