import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPessoa, getPessoaIdentifier } from '../pessoa.model';

export type EntityResponseType = HttpResponse<IPessoa>;
export type EntityArrayResponseType = HttpResponse<IPessoa[]>;

@Injectable({ providedIn: 'root' })
export class PessoaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pessoas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pessoa: IPessoa): Observable<EntityResponseType> {
    return this.http.post<IPessoa>(this.resourceUrl, pessoa, { observe: 'response' });
  }

  update(pessoa: IPessoa): Observable<EntityResponseType> {
    return this.http.put<IPessoa>(`${this.resourceUrl}/${getPessoaIdentifier(pessoa) as number}`, pessoa, { observe: 'response' });
  }

  partialUpdate(pessoa: IPessoa): Observable<EntityResponseType> {
    return this.http.patch<IPessoa>(`${this.resourceUrl}/${getPessoaIdentifier(pessoa) as number}`, pessoa, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPessoa>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPessoa[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPessoaToCollectionIfMissing(pessoaCollection: IPessoa[], ...pessoasToCheck: (IPessoa | null | undefined)[]): IPessoa[] {
    const pessoas: IPessoa[] = pessoasToCheck.filter(isPresent);
    if (pessoas.length > 0) {
      const pessoaCollectionIdentifiers = pessoaCollection.map(pessoaItem => getPessoaIdentifier(pessoaItem)!);
      const pessoasToAdd = pessoas.filter(pessoaItem => {
        const pessoaIdentifier = getPessoaIdentifier(pessoaItem);
        if (pessoaIdentifier == null || pessoaCollectionIdentifiers.includes(pessoaIdentifier)) {
          return false;
        }
        pessoaCollectionIdentifiers.push(pessoaIdentifier);
        return true;
      });
      return [...pessoasToAdd, ...pessoaCollection];
    }
    return pessoaCollection;
  }
}
