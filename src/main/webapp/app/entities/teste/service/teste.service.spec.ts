import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ITeste, Teste } from '../teste.model';

import { TesteService } from './teste.service';

describe('Teste Service', () => {
  let service: TesteService;
  let httpMock: HttpTestingController;
  let elemDefault: ITeste;
  let expectedResult: ITeste | ITeste[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TesteService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      idTeste: 0,
      dataTeste: currentDate,
      resultado: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dataTeste: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Teste', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dataTeste: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataTeste: currentDate,
        },
        returnedFromService
      );

      service.create(new Teste()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Teste', () => {
      const returnedFromService = Object.assign(
        {
          idTeste: 1,
          dataTeste: currentDate.format(DATE_FORMAT),
          resultado: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataTeste: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Teste', () => {
      const patchObject = Object.assign({}, new Teste());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dataTeste: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Teste', () => {
      const returnedFromService = Object.assign(
        {
          idTeste: 1,
          dataTeste: currentDate.format(DATE_FORMAT),
          resultado: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataTeste: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Teste', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTesteToCollectionIfMissing', () => {
      it('should add a Teste to an empty array', () => {
        const teste: ITeste = { idTeste: 123 };
        expectedResult = service.addTesteToCollectionIfMissing([], teste);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teste);
      });

      it('should not add a Teste to an array that contains it', () => {
        const teste: ITeste = { idTeste: 123 };
        const testeCollection: ITeste[] = [
          {
            ...teste,
          },
          { idTeste: 456 },
        ];
        expectedResult = service.addTesteToCollectionIfMissing(testeCollection, teste);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Teste to an array that doesn't contain it", () => {
        const teste: ITeste = { idTeste: 123 };
        const testeCollection: ITeste[] = [{ idTeste: 456 }];
        expectedResult = service.addTesteToCollectionIfMissing(testeCollection, teste);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teste);
      });

      it('should add only unique Teste to an array', () => {
        const testeArray: ITeste[] = [{ idTeste: 123 }, { idTeste: 456 }, { idTeste: 66166 }];
        const testeCollection: ITeste[] = [{ idTeste: 123 }];
        expectedResult = service.addTesteToCollectionIfMissing(testeCollection, ...testeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const teste: ITeste = { idTeste: 123 };
        const teste2: ITeste = { idTeste: 456 };
        expectedResult = service.addTesteToCollectionIfMissing([], teste, teste2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teste);
        expect(expectedResult).toContain(teste2);
      });

      it('should accept null and undefined values', () => {
        const teste: ITeste = { idTeste: 123 };
        expectedResult = service.addTesteToCollectionIfMissing([], null, teste, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teste);
      });

      it('should return initial array if no Teste is added', () => {
        const testeCollection: ITeste[] = [{ idTeste: 123 }];
        expectedResult = service.addTesteToCollectionIfMissing(testeCollection, undefined, null);
        expect(expectedResult).toEqual(testeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
