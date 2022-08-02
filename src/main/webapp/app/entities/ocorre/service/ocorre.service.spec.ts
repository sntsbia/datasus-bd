import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOcorre, Ocorre } from '../ocorre.model';

import { OcorreService } from './ocorre.service';

describe('Ocorre Service', () => {
  let service: OcorreService;
  let httpMock: HttpTestingController;
  let elemDefault: IOcorre;
  let expectedResult: IOcorre | IOcorre[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OcorreService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a Ocorre', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Ocorre()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ocorre', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ocorre', () => {
      const patchObject = Object.assign({}, new Ocorre());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ocorre', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a Ocorre', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOcorreToCollectionIfMissing', () => {
      it('should add a Ocorre to an empty array', () => {
        const ocorre: IOcorre = { id: 123 };
        expectedResult = service.addOcorreToCollectionIfMissing([], ocorre);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ocorre);
      });

      it('should not add a Ocorre to an array that contains it', () => {
        const ocorre: IOcorre = { id: 123 };
        const ocorreCollection: IOcorre[] = [
          {
            ...ocorre,
          },
          { id: 456 },
        ];
        expectedResult = service.addOcorreToCollectionIfMissing(ocorreCollection, ocorre);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ocorre to an array that doesn't contain it", () => {
        const ocorre: IOcorre = { id: 123 };
        const ocorreCollection: IOcorre[] = [{ id: 456 }];
        expectedResult = service.addOcorreToCollectionIfMissing(ocorreCollection, ocorre);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ocorre);
      });

      it('should add only unique Ocorre to an array', () => {
        const ocorreArray: IOcorre[] = [{ id: 123 }, { id: 456 }, { id: 3948 }];
        const ocorreCollection: IOcorre[] = [{ id: 123 }];
        expectedResult = service.addOcorreToCollectionIfMissing(ocorreCollection, ...ocorreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ocorre: IOcorre = { id: 123 };
        const ocorre2: IOcorre = { id: 456 };
        expectedResult = service.addOcorreToCollectionIfMissing([], ocorre, ocorre2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ocorre);
        expect(expectedResult).toContain(ocorre2);
      });

      it('should accept null and undefined values', () => {
        const ocorre: IOcorre = { id: 123 };
        expectedResult = service.addOcorreToCollectionIfMissing([], null, ocorre, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ocorre);
      });

      it('should return initial array if no Ocorre is added', () => {
        const ocorreCollection: IOcorre[] = [{ id: 123 }];
        expectedResult = service.addOcorreToCollectionIfMissing(ocorreCollection, undefined, null);
        expect(expectedResult).toEqual(ocorreCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
