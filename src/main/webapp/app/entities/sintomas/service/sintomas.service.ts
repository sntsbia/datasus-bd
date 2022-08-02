import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISintomas, getSintomasIdentifier } from '../sintomas.model';

export type EntityResponseType = HttpResponse<ISintomas>;
export type EntityArrayResponseType = HttpResponse<ISintomas[]>;

@Injectable({ providedIn: 'root' })
export class SintomasService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sintomas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(sintomas: ISintomas): Observable<EntityResponseType> {
    return this.http.post<ISintomas>(this.resourceUrl, sintomas, { observe: 'response' });
  }

  update(sintomas: ISintomas): Observable<EntityResponseType> {
    return this.http.put<ISintomas>(`${this.resourceUrl}/${getSintomasIdentifier(sintomas) as number}`, sintomas, { observe: 'response' });
  }

  partialUpdate(sintomas: ISintomas): Observable<EntityResponseType> {
    return this.http.patch<ISintomas>(`${this.resourceUrl}/${getSintomasIdentifier(sintomas) as number}`, sintomas, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISintomas>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISintomas[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSintomasToCollectionIfMissing(sintomasCollection: ISintomas[], ...sintomasToCheck: (ISintomas | null | undefined)[]): ISintomas[] {
    const sintomas: ISintomas[] = sintomasToCheck.filter(isPresent);
    if (sintomas.length > 0) {
      const sintomasCollectionIdentifiers = sintomasCollection.map(sintomasItem => getSintomasIdentifier(sintomasItem)!);
      const sintomasToAdd = sintomas.filter(sintomasItem => {
        const sintomasIdentifier = getSintomasIdentifier(sintomasItem);
        if (sintomasIdentifier == null || sintomasCollectionIdentifiers.includes(sintomasIdentifier)) {
          return false;
        }
        sintomasCollectionIdentifiers.push(sintomasIdentifier);
        return true;
      });
      return [...sintomasToAdd, ...sintomasCollection];
    }
    return sintomasCollection;
  }
}
