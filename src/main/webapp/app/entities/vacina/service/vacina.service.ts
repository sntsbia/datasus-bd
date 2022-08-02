import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVacina, getVacinaIdentifier } from '../vacina.model';

export type EntityResponseType = HttpResponse<IVacina>;
export type EntityArrayResponseType = HttpResponse<IVacina[]>;

@Injectable({ providedIn: 'root' })
export class VacinaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/vacinas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(vacina: IVacina): Observable<EntityResponseType> {
    return this.http.post<IVacina>(this.resourceUrl, vacina, { observe: 'response' });
  }

  update(vacina: IVacina): Observable<EntityResponseType> {
    return this.http.put<IVacina>(`${this.resourceUrl}/${getVacinaIdentifier(vacina) as number}`, vacina, { observe: 'response' });
  }

  partialUpdate(vacina: IVacina): Observable<EntityResponseType> {
    return this.http.patch<IVacina>(`${this.resourceUrl}/${getVacinaIdentifier(vacina) as number}`, vacina, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVacina>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVacina[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVacinaToCollectionIfMissing(vacinaCollection: IVacina[], ...vacinasToCheck: (IVacina | null | undefined)[]): IVacina[] {
    const vacinas: IVacina[] = vacinasToCheck.filter(isPresent);
    if (vacinas.length > 0) {
      const vacinaCollectionIdentifiers = vacinaCollection.map(vacinaItem => getVacinaIdentifier(vacinaItem)!);
      const vacinasToAdd = vacinas.filter(vacinaItem => {
        const vacinaIdentifier = getVacinaIdentifier(vacinaItem);
        if (vacinaIdentifier == null || vacinaCollectionIdentifiers.includes(vacinaIdentifier)) {
          return false;
        }
        vacinaCollectionIdentifiers.push(vacinaIdentifier);
        return true;
      });
      return [...vacinasToAdd, ...vacinaCollection];
    }
    return vacinaCollection;
  }
}
