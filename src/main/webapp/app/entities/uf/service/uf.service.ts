import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUf, getUfIdentifier } from '../uf.model';

export type EntityResponseType = HttpResponse<IUf>;
export type EntityArrayResponseType = HttpResponse<IUf[]>;

@Injectable({ providedIn: 'root' })
export class UfService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ufs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(uf: IUf): Observable<EntityResponseType> {
    return this.http.post<IUf>(this.resourceUrl, uf, { observe: 'response' });
  }

  update(uf: IUf): Observable<EntityResponseType> {
    return this.http.put<IUf>(`${this.resourceUrl}/${getUfIdentifier(uf) as number}`, uf, { observe: 'response' });
  }

  partialUpdate(uf: IUf): Observable<EntityResponseType> {
    return this.http.patch<IUf>(`${this.resourceUrl}/${getUfIdentifier(uf) as number}`, uf, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUf>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUf[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUfToCollectionIfMissing(ufCollection: IUf[], ...ufsToCheck: (IUf | null | undefined)[]): IUf[] {
    const ufs: IUf[] = ufsToCheck.filter(isPresent);
    if (ufs.length > 0) {
      const ufCollectionIdentifiers = ufCollection.map(ufItem => getUfIdentifier(ufItem)!);
      const ufsToAdd = ufs.filter(ufItem => {
        const ufIdentifier = getUfIdentifier(ufItem);
        if (ufIdentifier == null || ufCollectionIdentifiers.includes(ufIdentifier)) {
          return false;
        }
        ufCollectionIdentifiers.push(ufIdentifier);
        return true;
      });
      return [...ufsToAdd, ...ufCollection];
    }
    return ufCollection;
  }
}
