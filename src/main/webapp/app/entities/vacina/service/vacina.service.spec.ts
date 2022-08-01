import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IVacina, Vacina } from '../vacina.model';

import { VacinaService } from './vacina.service';

describe('Vacina Service', () => {
  let service: VacinaService;
  let httpMock: HttpTestingController;
  let elemDefault: IVacina;
  let expectedResult: IVacina | IVacina[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(VacinaService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      fabricante: 'AAAAAAA',
      nomeVacina: 'AAAAAAA',
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

    it('should create a Vacina', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Vacina()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Vacina', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          fabricante: 'BBBBBB',
          nomeVacina: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Vacina', () => {
      const patchObject = Object.assign({}, new Vacina());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Vacina', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          fabricante: 'BBBBBB',
          nomeVacina: 'BBBBBB',
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

    it('should delete a Vacina', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addVacinaToCollectionIfMissing', () => {
      it('should add a Vacina to an empty array', () => {
        const vacina: IVacina = { id: 123 };
        expectedResult = service.addVacinaToCollectionIfMissing([], vacina);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(vacina);
      });

      it('should not add a Vacina to an array that contains it', () => {
        const vacina: IVacina = { id: 123 };
        const vacinaCollection: IVacina[] = [
          {
            ...vacina,
          },
          { id: 456 },
        ];
        expectedResult = service.addVacinaToCollectionIfMissing(vacinaCollection, vacina);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Vacina to an array that doesn't contain it", () => {
        const vacina: IVacina = { id: 123 };
        const vacinaCollection: IVacina[] = [{ id: 456 }];
        expectedResult = service.addVacinaToCollectionIfMissing(vacinaCollection, vacina);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(vacina);
      });

      it('should add only unique Vacina to an array', () => {
        const vacinaArray: IVacina[] = [{ id: 123 }, { id: 456 }, { id: 97720 }];
        const vacinaCollection: IVacina[] = [{ id: 123 }];
        expectedResult = service.addVacinaToCollectionIfMissing(vacinaCollection, ...vacinaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const vacina: IVacina = { id: 123 };
        const vacina2: IVacina = { id: 456 };
        expectedResult = service.addVacinaToCollectionIfMissing([], vacina, vacina2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(vacina);
        expect(expectedResult).toContain(vacina2);
      });

      it('should accept null and undefined values', () => {
        const vacina: IVacina = { id: 123 };
        expectedResult = service.addVacinaToCollectionIfMissing([], null, vacina, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(vacina);
      });

      it('should return initial array if no Vacina is added', () => {
        const vacinaCollection: IVacina[] = [{ id: 123 }];
        expectedResult = service.addVacinaToCollectionIfMissing(vacinaCollection, undefined, null);
        expect(expectedResult).toEqual(vacinaCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
