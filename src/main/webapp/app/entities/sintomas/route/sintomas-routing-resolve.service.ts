import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISintomas, Sintomas } from '../sintomas.model';
import { SintomasService } from '../service/sintomas.service';

@Injectable({ providedIn: 'root' })
export class SintomasRoutingResolveService implements Resolve<ISintomas> {
  constructor(protected service: SintomasService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISintomas> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((sintomas: HttpResponse<Sintomas>) => {
          if (sintomas.body) {
            return of(sintomas.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Sintomas());
  }
}
