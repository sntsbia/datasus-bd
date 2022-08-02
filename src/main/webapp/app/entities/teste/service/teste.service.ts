import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeste, getTesteIdentifier } from '../teste.model';

export type EntityResponseType = HttpResponse<ITeste>;
export type EntityArrayResponseType = HttpResponse<ITeste[]>;

@Injectable({ providedIn: 'root' })
export class TesteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/testes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(teste: ITeste): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teste);
    return this.http
      .post<ITeste>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(teste: ITeste): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teste);
    return this.http
      .put<ITeste>(`${this.resourceUrl}/${getTesteIdentifier(teste) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(teste: ITeste): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(teste);
    return this.http
      .patch<ITeste>(`${this.resourceUrl}/${getTesteIdentifier(teste) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITeste>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITeste[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTesteToCollectionIfMissing(testeCollection: ITeste[], ...testesToCheck: (ITeste | null | undefined)[]): ITeste[] {
    const testes: ITeste[] = testesToCheck.filter(isPresent);
    if (testes.length > 0) {
      const testeCollectionIdentifiers = testeCollection.map(testeItem => getTesteIdentifier(testeItem)!);
      const testesToAdd = testes.filter(testeItem => {
        const testeIdentifier = getTesteIdentifier(testeItem);
        if (testeIdentifier == null || testeCollectionIdentifiers.includes(testeIdentifier)) {
          return false;
        }
        testeCollectionIdentifiers.push(testeIdentifier);
        return true;
      });
      return [...testesToAdd, ...testeCollection];
    }
    return testeCollection;
  }

  protected convertDateFromClient(teste: ITeste): ITeste {
    return Object.assign({}, teste, {
      dataTeste: teste.dataTeste?.isValid() ? teste.dataTeste.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dataTeste = res.body.dataTeste ? dayjs(res.body.dataTeste) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((teste: ITeste) => {
        teste.dataTeste = teste.dataTeste ? dayjs(teste.dataTeste) : undefined;
      });
    }
    return res;
  }
}
