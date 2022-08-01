import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IToma, Toma } from '../toma.model';

import { TomaService } from './toma.service';

describe('Toma Service', () => {
  let service: TomaService;
  let httpMock: HttpTestingController;
  let elemDefault: IToma;
  let expectedResult: IToma | IToma[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TomaService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      data: currentDate,
      lote: 'AAAAAAA',
      dose: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          data: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Toma', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          data: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          data: currentDate,
        },
        returnedFromService
      );

      service.create(new Toma()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Toma', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          data: currentDate.format(DATE_FORMAT),
          lote: 'BBBBBB',
          dose: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          data: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Toma', () => {
      const patchObject = Object.assign(
        {
          dose: 1,
        },
        new Toma()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          data: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Toma', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          data: currentDate.format(DATE_FORMAT),
          lote: 'BBBBBB',
          dose: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          data: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Toma', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTomaToCollectionIfMissing', () => {
      it('should add a Toma to an empty array', () => {
        const toma: IToma = { id: 123 };
        expectedResult = service.addTomaToCollectionIfMissing([], toma);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(toma);
      });

      it('should not add a Toma to an array that contains it', () => {
        const toma: IToma = { id: 123 };
        const tomaCollection: IToma[] = [
          {
            ...toma,
          },
          { id: 456 },
        ];
        expectedResult = service.addTomaToCollectionIfMissing(tomaCollection, toma);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Toma to an array that doesn't contain it", () => {
        const toma: IToma = { id: 123 };
        const tomaCollection: IToma[] = [{ id: 456 }];
        expectedResult = service.addTomaToCollectionIfMissing(tomaCollection, toma);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(toma);
      });

      it('should add only unique Toma to an array', () => {
        const tomaArray: IToma[] = [{ id: 123 }, { id: 456 }, { id: 21998 }];
        const tomaCollection: IToma[] = [{ id: 123 }];
        expectedResult = service.addTomaToCollectionIfMissing(tomaCollection, ...tomaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const toma: IToma = { id: 123 };
        const toma2: IToma = { id: 456 };
        expectedResult = service.addTomaToCollectionIfMissing([], toma, toma2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(toma);
        expect(expectedResult).toContain(toma2);
      });

      it('should accept null and undefined values', () => {
        const toma: IToma = { id: 123 };
        expectedResult = service.addTomaToCollectionIfMissing([], null, toma, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(toma);
      });

      it('should return initial array if no Toma is added', () => {
        const tomaCollection: IToma[] = [{ id: 123 }];
        expectedResult = service.addTomaToCollectionIfMissing(tomaCollection, undefined, null);
        expect(expectedResult).toEqual(tomaCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
