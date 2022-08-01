import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeste, Teste } from '../teste.model';
import { TesteService } from '../service/teste.service';

@Injectable({ providedIn: 'root' })
export class TesteRoutingResolveService implements Resolve<ITeste> {
  constructor(protected service: TesteService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITeste> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((teste: HttpResponse<Teste>) => {
          if (teste.body) {
            return of(teste.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Teste());
  }
}
