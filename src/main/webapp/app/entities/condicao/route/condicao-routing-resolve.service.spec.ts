import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ICondicao, Condicao } from '../condicao.model';
import { CondicaoService } from '../service/condicao.service';

import { CondicaoRoutingResolveService } from './condicao-routing-resolve.service';

describe('Condicao routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CondicaoRoutingResolveService;
  let service: CondicaoService;
  let resultCondicao: ICondicao | undefined;

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
    routingResolveService = TestBed.inject(CondicaoRoutingResolveService);
    service = TestBed.inject(CondicaoService);
    resultCondicao = undefined;
  });

  describe('resolve', () => {
    it('should return ICondicao returned by find', () => {
      // GIVEN
      service.find = jest.fn(idCondicao => of(new HttpResponse({ body: { idCondicao } })));
      mockActivatedRouteSnapshot.params = { idCondicao: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCondicao = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCondicao).toEqual({ idCondicao: 123 });
    });

    it('should return new ICondicao if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCondicao = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCondicao).toEqual(new Condicao());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Condicao })));
      mockActivatedRouteSnapshot.params = { idCondicao: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCondicao = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCondicao).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
