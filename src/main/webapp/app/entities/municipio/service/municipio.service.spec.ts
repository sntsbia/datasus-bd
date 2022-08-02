import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMunicipio, Municipio } from '../municipio.model';

import { MunicipioService } from './municipio.service';

describe('Municipio Service', () => {
  let service: MunicipioService;
  let httpMock: HttpTestingController;
  let elemDefault: IMunicipio;
  let expectedResult: IMunicipio | IMunicipio[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MunicipioService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      idMunicipio: 0,
      municipio: 'AAAAAAA',
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

    it('should create a Municipio', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Municipio()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Municipio', () => {
      const returnedFromService = Object.assign(
        {
          idMunicipio: 1,
          municipio: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Municipio', () => {
      const patchObject = Object.assign({}, new Municipio());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Municipio', () => {
      const returnedFromService = Object.assign(
        {
          idMunicipio: 1,
          municipio: 'BBBBBB',
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

    it('should delete a Municipio', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMunicipioToCollectionIfMissing', () => {
      it('should add a Municipio to an empty array', () => {
        const municipio: IMunicipio = { idMunicipio: 123 };
        expectedResult = service.addMunicipioToCollectionIfMissing([], municipio);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(municipio);
      });

      it('should not add a Municipio to an array that contains it', () => {
        const municipio: IMunicipio = { idMunicipio: 123 };
        const municipioCollection: IMunicipio[] = [
          {
            ...municipio,
          },
          { idMunicipio: 456 },
        ];
        expectedResult = service.addMunicipioToCollectionIfMissing(municipioCollection, municipio);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Municipio to an array that doesn't contain it", () => {
        const municipio: IMunicipio = { idMunicipio: 123 };
        const municipioCollection: IMunicipio[] = [{ idMunicipio: 456 }];
        expectedResult = service.addMunicipioToCollectionIfMissing(municipioCollection, municipio);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(municipio);
      });

      it('should add only unique Municipio to an array', () => {
        const municipioArray: IMunicipio[] = [{ idMunicipio: 123 }, { idMunicipio: 456 }, { idMunicipio: 25743 }];
        const municipioCollection: IMunicipio[] = [{ idMunicipio: 123 }];
        expectedResult = service.addMunicipioToCollectionIfMissing(municipioCollection, ...municipioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const municipio: IMunicipio = { idMunicipio: 123 };
        const municipio2: IMunicipio = { idMunicipio: 456 };
        expectedResult = service.addMunicipioToCollectionIfMissing([], municipio, municipio2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(municipio);
        expect(expectedResult).toContain(municipio2);
      });

      it('should accept null and undefined values', () => {
        const municipio: IMunicipio = { idMunicipio: 123 };
        expectedResult = service.addMunicipioToCollectionIfMissing([], null, municipio, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(municipio);
      });

      it('should return initial array if no Municipio is added', () => {
        const municipioCollection: IMunicipio[] = [{ idMunicipio: 123 }];
        expectedResult = service.addMunicipioToCollectionIfMissing(municipioCollection, undefined, null);
        expect(expectedResult).toEqual(municipioCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
