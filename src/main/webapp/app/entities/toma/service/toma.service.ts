import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IToma, getTomaIdentifier } from '../toma.model';

export type EntityResponseType = HttpResponse<IToma>;
export type EntityArrayResponseType = HttpResponse<IToma[]>;

@Injectable({ providedIn: 'root' })
export class TomaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tomas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(toma: IToma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(toma);
    return this.http
      .post<IToma>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(toma: IToma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(toma);
    return this.http
      .put<IToma>(`${this.resourceUrl}/${getTomaIdentifier(toma) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(toma: IToma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(toma);
    return this.http
      .patch<IToma>(`${this.resourceUrl}/${getTomaIdentifier(toma) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IToma>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IToma[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTomaToCollectionIfMissing(tomaCollection: IToma[], ...tomasToCheck: (IToma | null | undefined)[]): IToma[] {
    const tomas: IToma[] = tomasToCheck.filter(isPresent);
    if (tomas.length > 0) {
      const tomaCollectionIdentifiers = tomaCollection.map(tomaItem => getTomaIdentifier(tomaItem)!);
      const tomasToAdd = tomas.filter(tomaItem => {
        const tomaIdentifier = getTomaIdentifier(tomaItem);
        if (tomaIdentifier == null || tomaCollectionIdentifiers.includes(tomaIdentifier)) {
          return false;
        }
        tomaCollectionIdentifiers.push(tomaIdentifier);
        return true;
      });
      return [...tomasToAdd, ...tomaCollection];
    }
    return tomaCollection;
  }

  protected convertDateFromClient(toma: IToma): IToma {
    return Object.assign({}, toma, {
      data: toma.data?.isValid() ? toma.data.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.data = res.body.data ? dayjs(res.body.data) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((toma: IToma) => {
        toma.data = toma.data ? dayjs(toma.data) : undefined;
      });
    }
    return res;
  }
}
