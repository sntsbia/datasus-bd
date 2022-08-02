import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICondicao, Condicao } from '../condicao.model';
import { CondicaoService } from '../service/condicao.service';

@Injectable({ providedIn: 'root' })
export class CondicaoRoutingResolveService implements Resolve<ICondicao> {
  constructor(protected service: CondicaoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICondicao> | Observable<never> {
    const id = route.params['idCondicao'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((condicao: HttpResponse<Condicao>) => {
          if (condicao.body) {
            return of(condicao.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Condicao());
  }
}
