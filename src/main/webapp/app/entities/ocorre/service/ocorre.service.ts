import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOcorre, getOcorreIdentifier } from '../ocorre.model';

export type EntityResponseType = HttpResponse<IOcorre>;
export type EntityArrayResponseType = HttpResponse<IOcorre[]>;

@Injectable({ providedIn: 'root' })
export class OcorreService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ocorres');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ocorre: IOcorre): Observable<EntityResponseType> {
    return this.http.post<IOcorre>(this.resourceUrl, ocorre, { observe: 'response' });
  }

  update(ocorre: IOcorre): Observable<EntityResponseType> {
    return this.http.put<IOcorre>(`${this.resourceUrl}/${getOcorreIdentifier(ocorre) as number}`, ocorre, { observe: 'response' });
  }

  partialUpdate(ocorre: IOcorre): Observable<EntityResponseType> {
    return this.http.patch<IOcorre>(`${this.resourceUrl}/${getOcorreIdentifier(ocorre) as number}`, ocorre, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOcorre>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOcorre[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOcorreToCollectionIfMissing(ocorreCollection: IOcorre[], ...ocorresToCheck: (IOcorre | null | undefined)[]): IOcorre[] {
    const ocorres: IOcorre[] = ocorresToCheck.filter(isPresent);
    if (ocorres.length > 0) {
      const ocorreCollectionIdentifiers = ocorreCollection.map(ocorreItem => getOcorreIdentifier(ocorreItem)!);
      const ocorresToAdd = ocorres.filter(ocorreItem => {
        const ocorreIdentifier = getOcorreIdentifier(ocorreItem);
        if (ocorreIdentifier == null || ocorreCollectionIdentifiers.includes(ocorreIdentifier)) {
          return false;
        }
        ocorreCollectionIdentifiers.push(ocorreIdentifier);
        return true;
      });
      return [...ocorresToAdd, ...ocorreCollection];
    }
    return ocorreCollection;
  }
}
