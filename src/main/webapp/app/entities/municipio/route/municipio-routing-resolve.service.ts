import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMunicipio, Municipio } from '../municipio.model';
import { MunicipioService } from '../service/municipio.service';

@Injectable({ providedIn: 'root' })
export class MunicipioRoutingResolveService implements Resolve<IMunicipio> {
  constructor(protected service: MunicipioService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMunicipio> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((municipio: HttpResponse<Municipio>) => {
          if (municipio.body) {
            return of(municipio.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Municipio());
  }
}
