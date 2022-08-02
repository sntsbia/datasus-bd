import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Sexo } from 'app/entities/enumerations/sexo.model';
import { IPessoa, Pessoa } from '../pessoa.model';

import { PessoaService } from './pessoa.service';

describe('Pessoa Service', () => {
  let service: PessoaService;
  let httpMock: HttpTestingController;
  let elemDefault: IPessoa;
  let expectedResult: IPessoa | IPessoa[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PessoaService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      idPessoa: 0,
      sexo: Sexo.F,
      idade: 0,
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

    it('should create a Pessoa', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Pessoa()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Pessoa', () => {
      const returnedFromService = Object.assign(
        {
          idPessoa: 1,
          sexo: 'BBBBBB',
          idade: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Pessoa', () => {
      const patchObject = Object.assign(
        {
          sexo: 'BBBBBB',
        },
        new Pessoa()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Pessoa', () => {
      const returnedFromService = Object.assign(
        {
          idPessoa: 1,
          sexo: 'BBBBBB',
          idade: 1,
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

    it('should delete a Pessoa', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPessoaToCollectionIfMissing', () => {
      it('should add a Pessoa to an empty array', () => {
        const pessoa: IPessoa = { idPessoa: 123 };
        expectedResult = service.addPessoaToCollectionIfMissing([], pessoa);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pessoa);
      });

      it('should not add a Pessoa to an array that contains it', () => {
        const pessoa: IPessoa = { idPessoa: 123 };
        const pessoaCollection: IPessoa[] = [
          {
            ...pessoa,
          },
          { idPessoa: 456 },
        ];
        expectedResult = service.addPessoaToCollectionIfMissing(pessoaCollection, pessoa);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Pessoa to an array that doesn't contain it", () => {
        const pessoa: IPessoa = { idPessoa: 123 };
        const pessoaCollection: IPessoa[] = [{ idPessoa: 456 }];
        expectedResult = service.addPessoaToCollectionIfMissing(pessoaCollection, pessoa);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pessoa);
      });

      it('should add only unique Pessoa to an array', () => {
        const pessoaArray: IPessoa[] = [{ idPessoa: 123 }, { idPessoa: 456 }, { idPessoa: 83865 }];
        const pessoaCollection: IPessoa[] = [{ idPessoa: 123 }];
        expectedResult = service.addPessoaToCollectionIfMissing(pessoaCollection, ...pessoaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pessoa: IPessoa = { idPessoa: 123 };
        const pessoa2: IPessoa = { idPessoa: 456 };
        expectedResult = service.addPessoaToCollectionIfMissing([], pessoa, pessoa2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pessoa);
        expect(expectedResult).toContain(pessoa2);
      });

      it('should accept null and undefined values', () => {
        const pessoa: IPessoa = { idPessoa: 123 };
        expectedResult = service.addPessoaToCollectionIfMissing([], null, pessoa, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pessoa);
      });

      it('should return initial array if no Pessoa is added', () => {
        const pessoaCollection: IPessoa[] = [{ idPessoa: 123 }];
        expectedResult = service.addPessoaToCollectionIfMissing(pessoaCollection, undefined, null);
        expect(expectedResult).toEqual(pessoaCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
