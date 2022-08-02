import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMunicipio, getMunicipioIdentifier } from '../municipio.model';

export type EntityResponseType = HttpResponse<IMunicipio>;
export type EntityArrayResponseType = HttpResponse<IMunicipio[]>;

@Injectable({ providedIn: 'root' })
export class MunicipioService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/municipios');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(municipio: IMunicipio): Observable<EntityResponseType> {
    return this.http.post<IMunicipio>(this.resourceUrl, municipio, { observe: 'response' });
  }

  update(municipio: IMunicipio): Observable<EntityResponseType> {
    return this.http.put<IMunicipio>(`${this.resourceUrl}/${getMunicipioIdentifier(municipio) as number}`, municipio, {
      observe: 'response',
    });
  }

  partialUpdate(municipio: IMunicipio): Observable<EntityResponseType> {
    return this.http.patch<IMunicipio>(`${this.resourceUrl}/${getMunicipioIdentifier(municipio) as number}`, municipio, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMunicipio>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMunicipio[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMunicipioToCollectionIfMissing(
    municipioCollection: IMunicipio[],
    ...municipiosToCheck: (IMunicipio | null | undefined)[]
  ): IMunicipio[] {
    const municipios: IMunicipio[] = municipiosToCheck.filter(isPresent);
    if (municipios.length > 0) {
      const municipioCollectionIdentifiers = municipioCollection.map(municipioItem => getMunicipioIdentifier(municipioItem)!);
      const municipiosToAdd = municipios.filter(municipioItem => {
        const municipioIdentifier = getMunicipioIdentifier(municipioItem);
        if (municipioIdentifier == null || municipioCollectionIdentifiers.includes(municipioIdentifier)) {
          return false;
        }
        municipioCollectionIdentifiers.push(municipioIdentifier);
        return true;
      });
      return [...municipiosToAdd, ...municipioCollection];
    }
    return municipioCollection;
  }
}
