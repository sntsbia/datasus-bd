import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMora, Mora } from '../mora.model';

import { MoraService } from './mora.service';

describe('Mora Service', () => {
  let service: MoraService;
  let httpMock: HttpTestingController;
  let elemDefault: IMora;
  let expectedResult: IMora | IMora[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MoraService);
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

    it('should create a Mora', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Mora()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Mora', () => {
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

    it('should partial update a Mora', () => {
      const patchObject = Object.assign({}, new Mora());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Mora', () => {
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

    it('should delete a Mora', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMoraToCollectionIfMissing', () => {
      it('should add a Mora to an empty array', () => {
        const mora: IMora = { id: 123 };
        expectedResult = service.addMoraToCollectionIfMissing([], mora);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mora);
      });

      it('should not add a Mora to an array that contains it', () => {
        const mora: IMora = { id: 123 };
        const moraCollection: IMora[] = [
          {
            ...mora,
          },
          { id: 456 },
        ];
        expectedResult = service.addMoraToCollectionIfMissing(moraCollection, mora);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Mora to an array that doesn't contain it", () => {
        const mora: IMora = { id: 123 };
        const moraCollection: IMora[] = [{ id: 456 }];
        expectedResult = service.addMoraToCollectionIfMissing(moraCollection, mora);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mora);
      });

      it('should add only unique Mora to an array', () => {
        const moraArray: IMora[] = [{ id: 123 }, { id: 456 }, { id: 13797 }];
        const moraCollection: IMora[] = [{ id: 123 }];
        expectedResult = service.addMoraToCollectionIfMissing(moraCollection, ...moraArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mora: IMora = { id: 123 };
        const mora2: IMora = { id: 456 };
        expectedResult = service.addMoraToCollectionIfMissing([], mora, mora2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mora);
        expect(expectedResult).toContain(mora2);
      });

      it('should accept null and undefined values', () => {
        const mora: IMora = { id: 123 };
        expectedResult = service.addMoraToCollectionIfMissing([], null, mora, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mora);
      });

      it('should return initial array if no Mora is added', () => {
        const moraCollection: IMora[] = [{ id: 123 }];
        expectedResult = service.addMoraToCollectionIfMissing(moraCollection, undefined, null);
        expect(expectedResult).toEqual(moraCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
