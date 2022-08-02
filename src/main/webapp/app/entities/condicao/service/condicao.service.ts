import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICondicao, getCondicaoIdentifier } from '../condicao.model';

export type EntityResponseType = HttpResponse<ICondicao>;
export type EntityArrayResponseType = HttpResponse<ICondicao[]>;

@Injectable({ providedIn: 'root' })
export class CondicaoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/condicaos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(condicao: ICondicao): Observable<EntityResponseType> {
    return this.http.post<ICondicao>(this.resourceUrl, condicao, { observe: 'response' });
  }

  update(condicao: ICondicao): Observable<EntityResponseType> {
    return this.http.put<ICondicao>(`${this.resourceUrl}/${getCondicaoIdentifier(condicao) as number}`, condicao, { observe: 'response' });
  }

  partialUpdate(condicao: ICondicao): Observable<EntityResponseType> {
    return this.http.patch<ICondicao>(`${this.resourceUrl}/${getCondicaoIdentifier(condicao) as number}`, condicao, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICondicao>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICondicao[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCondicaoToCollectionIfMissing(condicaoCollection: ICondicao[], ...condicaosToCheck: (ICondicao | null | undefined)[]): ICondicao[] {
    const condicaos: ICondicao[] = condicaosToCheck.filter(isPresent);
    if (condicaos.length > 0) {
      const condicaoCollectionIdentifiers = condicaoCollection.map(condicaoItem => getCondicaoIdentifier(condicaoItem)!);
      const condicaosToAdd = condicaos.filter(condicaoItem => {
        const condicaoIdentifier = getCondicaoIdentifier(condicaoItem);
        if (condicaoIdentifier == null || condicaoCollectionIdentifiers.includes(condicaoIdentifier)) {
          return false;
        }
        condicaoCollectionIdentifiers.push(condicaoIdentifier);
        return true;
      });
      return [...condicaosToAdd, ...condicaoCollection];
    }
    return condicaoCollection;
  }
}
