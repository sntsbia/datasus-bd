import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISintomas, Sintomas } from '../sintomas.model';

import { SintomasService } from './sintomas.service';

describe('Sintomas Service', () => {
  let service: SintomasService;
  let httpMock: HttpTestingController;
  let elemDefault: ISintomas;
  let expectedResult: ISintomas | ISintomas[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SintomasService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      idSintomas: 0,
      sintomas: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Sintomas', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Sintomas()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Sintomas', () => {
      const returnedFromService = Object.assign(
        {
          idSintomas: 1,
          sintomas: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Sintomas', () => {
      const patchObject = Object.assign({}, new Sintomas());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Sintomas', () => {
      const returnedFromService = Object.assign(
        {
          idSintomas: 1,
          sintomas: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Sintomas', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSintomasToCollectionIfMissing', () => {
      it('should add a Sintomas to an empty array', () => {
        const sintomas: ISintomas = { idSintomas: 123 };
        expectedResult = service.addSintomasToCollectionIfMissing([], sintomas);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sintomas);
      });

      it('should not add a Sintomas to an array that contains it', () => {
        const sintomas: ISintomas = { idSintomas: 123 };
        const sintomasCollection: ISintomas[] = [
          {
            ...sintomas,
          },
          { idSintomas: 456 },
        ];
        expectedResult = service.addSintomasToCollectionIfMissing(sintomasCollection, sintomas);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Sintomas to an array that doesn't contain it", () => {
        const sintomas: ISintomas = { idSintomas: 123 };
        const sintomasCollection: ISintomas[] = [{ idSintomas: 456 }];
        expectedResult = service.addSintomasToCollectionIfMissing(sintomasCollection, sintomas);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sintomas);
      });

      it('should add only unique Sintomas to an array', () => {
        const sintomasArray: ISintomas[] = [{ idSintomas: 123 }, { idSintomas: 456 }, { idSintomas: 42744 }];
        const sintomasCollection: ISintomas[] = [{ idSintomas: 123 }];
        expectedResult = service.addSintomasToCollectionIfMissing(sintomasCollection, ...sintomasArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sintomas: ISintomas = { idSintomas: 123 };
        const sintomas2: ISintomas = { idSintomas: 456 };
        expectedResult = service.addSintomasToCollectionIfMissing([], sintomas, sintomas2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sintomas);
        expect(expectedResult).toContain(sintomas2);
      });

      it('should accept null and undefined values', () => {
        const sintomas: ISintomas = { idSintomas: 123 };
        expectedResult = service.addSintomasToCollectionIfMissing([], null, sintomas, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sintomas);
      });

      it('should return initial array if no Sintomas is added', () => {
        const sintomasCollection: ISintomas[] = [{ idSintomas: 123 }];
        expectedResult = service.addSintomasToCollectionIfMissing(sintomasCollection, undefined, null);
        expect(expectedResult).toEqual(sintomasCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
