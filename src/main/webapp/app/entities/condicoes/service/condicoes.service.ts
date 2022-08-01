import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICondicoes, getCondicoesIdentifier } from '../condicoes.model';

export type EntityResponseType = HttpResponse<ICondicoes>;
export type EntityArrayResponseType = HttpResponse<ICondicoes[]>;

@Injectable({ providedIn: 'root' })
export class CondicoesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/condicoes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(condicoes: ICondicoes): Observable<EntityResponseType> {
    return this.http.post<ICondicoes>(this.resourceUrl, condicoes, { observe: 'response' });
  }

  update(condicoes: ICondicoes): Observable<EntityResponseType> {
    return this.http.put<ICondicoes>(`${this.resourceUrl}/${getCondicoesIdentifier(condicoes) as number}`, condicoes, {
      observe: 'response',
    });
  }

  partialUpdate(condicoes: ICondicoes): Observable<EntityResponseType> {
    return this.http.patch<ICondicoes>(`${this.resourceUrl}/${getCondicoesIdentifier(condicoes) as number}`, condicoes, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICondicoes>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICondicoes[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCondicoesToCollectionIfMissing(
    condicoesCollection: ICondicoes[],
    ...condicoesToCheck: (ICondicoes | null | undefined)[]
  ): ICondicoes[] {
    const condicoes: ICondicoes[] = condicoesToCheck.filter(isPresent);
    if (condicoes.length > 0) {
      const condicoesCollectionIdentifiers = condicoesCollection.map(condicoesItem => getCondicoesIdentifier(condicoesItem)!);
      const condicoesToAdd = condicoes.filter(condicoesItem => {
        const condicoesIdentifier = getCondicoesIdentifier(condicoesItem);
        if (condicoesIdentifier == null || condicoesCollectionIdentifiers.includes(condicoesIdentifier)) {
          return false;
        }
        condicoesCollectionIdentifiers.push(condicoesIdentifier);
        return true;
      });
      return [...condicoesToAdd, ...condicoesCollection];
    }
    return condicoesCollection;
  }
}
