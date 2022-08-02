import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICondicao, Condicao } from '../condicao.model';

import { CondicaoService } from './condicao.service';

describe('Condicao Service', () => {
  let service: CondicaoService;
  let httpMock: HttpTestingController;
  let elemDefault: ICondicao;
  let expectedResult: ICondicao | ICondicao[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CondicaoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      idCondicao: 0,
      condicao: 'AAAAAAA',
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

    it('should create a Condicao', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Condicao()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Condicao', () => {
      const returnedFromService = Object.assign(
        {
          idCondicao: 1,
          condicao: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Condicao', () => {
      const patchObject = Object.assign({}, new Condicao());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Condicao', () => {
      const returnedFromService = Object.assign(
        {
          idCondicao: 1,
          condicao: 'BBBBBB',
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

    it('should delete a Condicao', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCondicaoToCollectionIfMissing', () => {
      it('should add a Condicao to an empty array', () => {
        const condicao: ICondicao = { idCondicao: 123 };
        expectedResult = service.addCondicaoToCollectionIfMissing([], condicao);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(condicao);
      });

      it('should not add a Condicao to an array that contains it', () => {
        const condicao: ICondicao = { idCondicao: 123 };
        const condicaoCollection: ICondicao[] = [
          {
            ...condicao,
          },
          { idCondicao: 456 },
        ];
        expectedResult = service.addCondicaoToCollectionIfMissing(condicaoCollection, condicao);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Condicao to an array that doesn't contain it", () => {
        const condicao: ICondicao = { idCondicao: 123 };
        const condicaoCollection: ICondicao[] = [{ idCondicao: 456 }];
        expectedResult = service.addCondicaoToCollectionIfMissing(condicaoCollection, condicao);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(condicao);
      });

      it('should add only unique Condicao to an array', () => {
        const condicaoArray: ICondicao[] = [{ idCondicao: 123 }, { idCondicao: 456 }, { idCondicao: 54879 }];
        const condicaoCollection: ICondicao[] = [{ idCondicao: 123 }];
        expectedResult = service.addCondicaoToCollectionIfMissing(condicaoCollection, ...condicaoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const condicao: ICondicao = { idCondicao: 123 };
        const condicao2: ICondicao = { idCondicao: 456 };
        expectedResult = service.addCondicaoToCollectionIfMissing([], condicao, condicao2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(condicao);
        expect(expectedResult).toContain(condicao2);
      });

      it('should accept null and undefined values', () => {
        const condicao: ICondicao = { idCondicao: 123 };
        expectedResult = service.addCondicaoToCollectionIfMissing([], null, condicao, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(condicao);
      });

      it('should return initial array if no Condicao is added', () => {
        const condicaoCollection: ICondicao[] = [{ idCondicao: 123 }];
        expectedResult = service.addCondicaoToCollectionIfMissing(condicaoCollection, undefined, null);
        expect(expectedResult).toEqual(condicaoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
