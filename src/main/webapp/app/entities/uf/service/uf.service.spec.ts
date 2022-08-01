import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUf, Uf } from '../uf.model';

import { UfService } from './uf.service';

describe('Uf Service', () => {
  let service: UfService;
  let httpMock: HttpTestingController;
  let elemDefault: IUf;
  let expectedResult: IUf | IUf[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UfService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      codigoIbge: 0,
      estado: 'AAAAAAA',
      bandeiraContentType: 'image/png',
      bandeira: 'AAAAAAA',
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

    it('should create a Uf', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Uf()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Uf', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          codigoIbge: 1,
          estado: 'BBBBBB',
          bandeira: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Uf', () => {
      const patchObject = Object.assign(
        {
          codigoIbge: 1,
          bandeira: 'BBBBBB',
        },
        new Uf()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Uf', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          codigoIbge: 1,
          estado: 'BBBBBB',
          bandeira: 'BBBBBB',
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

    it('should delete a Uf', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addUfToCollectionIfMissing', () => {
      it('should add a Uf to an empty array', () => {
        const uf: IUf = { id: 123 };
        expectedResult = service.addUfToCollectionIfMissing([], uf);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(uf);
      });

      it('should not add a Uf to an array that contains it', () => {
        const uf: IUf = { id: 123 };
        const ufCollection: IUf[] = [
          {
            ...uf,
          },
          { id: 456 },
        ];
        expectedResult = service.addUfToCollectionIfMissing(ufCollection, uf);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Uf to an array that doesn't contain it", () => {
        const uf: IUf = { id: 123 };
        const ufCollection: IUf[] = [{ id: 456 }];
        expectedResult = service.addUfToCollectionIfMissing(ufCollection, uf);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(uf);
      });

      it('should add only unique Uf to an array', () => {
        const ufArray: IUf[] = [{ id: 123 }, { id: 456 }, { id: 84480 }];
        const ufCollection: IUf[] = [{ id: 123 }];
        expectedResult = service.addUfToCollectionIfMissing(ufCollection, ...ufArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const uf: IUf = { id: 123 };
        const uf2: IUf = { id: 456 };
        expectedResult = service.addUfToCollectionIfMissing([], uf, uf2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(uf);
        expect(expectedResult).toContain(uf2);
      });

      it('should accept null and undefined values', () => {
        const uf: IUf = { id: 123 };
        expectedResult = service.addUfToCollectionIfMissing([], null, uf, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(uf);
      });

      it('should return initial array if no Uf is added', () => {
        const ufCollection: IUf[] = [{ id: 123 }];
        expectedResult = service.addUfToCollectionIfMissing(ufCollection, undefined, null);
        expect(expectedResult).toEqual(ufCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
