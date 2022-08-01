import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMora, getMoraIdentifier } from '../mora.model';

export type EntityResponseType = HttpResponse<IMora>;
export type EntityArrayResponseType = HttpResponse<IMora[]>;

@Injectable({ providedIn: 'root' })
export class MoraService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/moras');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(mora: IMora): Observable<EntityResponseType> {
    return this.http.post<IMora>(this.resourceUrl, mora, { observe: 'response' });
  }

  update(mora: IMora): Observable<EntityResponseType> {
    return this.http.put<IMora>(`${this.resourceUrl}/${getMoraIdentifier(mora) as number}`, mora, { observe: 'response' });
  }

  partialUpdate(mora: IMora): Observable<EntityResponseType> {
    return this.http.patch<IMora>(`${this.resourceUrl}/${getMoraIdentifier(mora) as number}`, mora, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMora>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMora[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMoraToCollectionIfMissing(moraCollection: IMora[], ...morasToCheck: (IMora | null | undefined)[]): IMora[] {
    const moras: IMora[] = morasToCheck.filter(isPresent);
    if (moras.length > 0) {
      const moraCollectionIdentifiers = moraCollection.map(moraItem => getMoraIdentifier(moraItem)!);
      const morasToAdd = moras.filter(moraItem => {
        const moraIdentifier = getMoraIdentifier(moraItem);
        if (moraIdentifier == null || moraCollectionIdentifiers.includes(moraIdentifier)) {
          return false;
        }
        moraCollectionIdentifiers.push(moraIdentifier);
        return true;
      });
      return [...morasToAdd, ...moraCollection];
    }
    return moraCollection;
  }
}
