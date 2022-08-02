import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IPessoa, Pessoa } from '../pessoa.model';
import { PessoaService } from '../service/pessoa.service';

import { PessoaRoutingResolveService } from './pessoa-routing-resolve.service';

describe('Pessoa routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: PessoaRoutingResolveService;
  let service: PessoaService;
  let resultPessoa: IPessoa | undefined;

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
    routingResolveService = TestBed.inject(PessoaRoutingResolveService);
    service = TestBed.inject(PessoaService);
    resultPessoa = undefined;
  });

  describe('resolve', () => {
    it('should return IPessoa returned by find', () => {
      // GIVEN
      service.find = jest.fn(idPessoa => of(new HttpResponse({ body: { idPessoa } })));
      mockActivatedRouteSnapshot.params = { idPessoa: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPessoa = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPessoa).toEqual({ idPessoa: 123 });
    });

    it('should return new IPessoa if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPessoa = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPessoa).toEqual(new Pessoa());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Pessoa })));
      mockActivatedRouteSnapshot.params = { idPessoa: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPessoa = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPessoa).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
