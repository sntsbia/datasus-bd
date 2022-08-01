import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICondicoes, Condicoes } from '../condicoes.model';

import { CondicoesService } from './condicoes.service';

describe('Condicoes Service', () => {
  let service: CondicoesService;
  let httpMock: HttpTestingController;
  let elemDefault: ICondicoes;
  let expectedResult: ICondicoes | ICondicoes[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CondicoesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should create a Condicoes', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Condicoes()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Condicoes', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should partial update a Condicoes', () => {
      const patchObject = Object.assign(
        {
          condicao: 'BBBBBB',
        },
        new Condicoes()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Condicoes', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    it('should delete a Condicoes', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCondicoesToCollectionIfMissing', () => {
      it('should add a Condicoes to an empty array', () => {
        const condicoes: ICondicoes = { id: 123 };
        expectedResult = service.addCondicoesToCollectionIfMissing([], condicoes);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(condicoes);
      });

      it('should not add a Condicoes to an array that contains it', () => {
        const condicoes: ICondicoes = { id: 123 };
        const condicoesCollection: ICondicoes[] = [
          {
            ...condicoes,
          },
          { id: 456 },
        ];
        expectedResult = service.addCondicoesToCollectionIfMissing(condicoesCollection, condicoes);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Condicoes to an array that doesn't contain it", () => {
        const condicoes: ICondicoes = { id: 123 };
        const condicoesCollection: ICondicoes[] = [{ id: 456 }];
        expectedResult = service.addCondicoesToCollectionIfMissing(condicoesCollection, condicoes);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(condicoes);
      });

      it('should add only unique Condicoes to an array', () => {
        const condicoesArray: ICondicoes[] = [{ id: 123 }, { id: 456 }, { id: 37439 }];
        const condicoesCollection: ICondicoes[] = [{ id: 123 }];
        expectedResult = service.addCondicoesToCollectionIfMissing(condicoesCollection, ...condicoesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const condicoes: ICondicoes = { id: 123 };
        const condicoes2: ICondicoes = { id: 456 };
        expectedResult = service.addCondicoesToCollectionIfMissing([], condicoes, condicoes2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(condicoes);
        expect(expectedResult).toContain(condicoes2);
      });

      it('should accept null and undefined values', () => {
        const condicoes: ICondicoes = { id: 123 };
        expectedResult = service.addCondicoesToCollectionIfMissing([], null, condicoes, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(condicoes);
      });

      it('should return initial array if no Condicoes is added', () => {
        const condicoesCollection: ICondicoes[] = [{ id: 123 }];
        expectedResult = service.addCondicoesToCollectionIfMissing(condicoesCollection, undefined, null);
        expect(expectedResult).toEqual(condicoesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
