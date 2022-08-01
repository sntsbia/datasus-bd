import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVacina, Vacina } from '../vacina.model';
import { VacinaService } from '../service/vacina.service';

@Injectable({ providedIn: 'root' })
export class VacinaRoutingResolveService implements Resolve<IVacina> {
  constructor(protected service: VacinaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IVacina> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((vacina: HttpResponse<Vacina>) => {
          if (vacina.body) {
            return of(vacina.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Vacina());
  }
}
