import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICondicoes, Condicoes } from '../condicoes.model';
import { CondicoesService } from '../service/condicoes.service';

@Injectable({ providedIn: 'root' })
export class CondicoesRoutingResolveService implements Resolve<ICondicoes> {
  constructor(protected service: CondicoesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICondicoes> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((condicoes: HttpResponse<Condicoes>) => {
          if (condicoes.body) {
            return of(condicoes.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Condicoes());
  }
}
