import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IUf, Uf } from '../uf.model';
import { UfService } from '../service/uf.service';

import { UfRoutingResolveService } from './uf-routing-resolve.service';

describe('Uf routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: UfRoutingResolveService;
  let service: UfService;
  let resultUf: IUf | undefined;

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
    routingResolveService = TestBed.inject(UfRoutingResolveService);
    service = TestBed.inject(UfService);
    resultUf = undefined;
  });

  describe('resolve', () => {
    it('should return IUf returned by find', () => {
      // GIVEN
      service.find = jest.fn(idUf => of(new HttpResponse({ body: { idUf } })));
      mockActivatedRouteSnapshot.params = { idUf: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUf = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultUf).toEqual({ idUf: 123 });
    });

    it('should return new IUf if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUf = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultUf).toEqual(new Uf());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Uf })));
      mockActivatedRouteSnapshot.params = { idUf: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultUf = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultUf).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
