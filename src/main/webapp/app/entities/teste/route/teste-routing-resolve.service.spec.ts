import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITeste, Teste } from '../teste.model';
import { TesteService } from '../service/teste.service';

import { TesteRoutingResolveService } from './teste-routing-resolve.service';

describe('Teste routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TesteRoutingResolveService;
  let service: TesteService;
  let resultTeste: ITeste | undefined;

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
    routingResolveService = TestBed.inject(TesteRoutingResolveService);
    service = TestBed.inject(TesteService);
    resultTeste = undefined;
  });

  describe('resolve', () => {
    it('should return ITeste returned by find', () => {
      // GIVEN
      service.find = jest.fn(idTeste => of(new HttpResponse({ body: { idTeste } })));
      mockActivatedRouteSnapshot.params = { idTeste: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTeste = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTeste).toEqual({ idTeste: 123 });
    });

    it('should return new ITeste if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTeste = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTeste).toEqual(new Teste());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Teste })));
      mockActivatedRouteSnapshot.params = { idTeste: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTeste = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTeste).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
