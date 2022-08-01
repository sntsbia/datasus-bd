import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IToma, Toma } from '../toma.model';
import { TomaService } from '../service/toma.service';

@Injectable({ providedIn: 'root' })
export class TomaRoutingResolveService implements Resolve<IToma> {
  constructor(protected service: TomaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IToma> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((toma: HttpResponse<Toma>) => {
          if (toma.body) {
            return of(toma.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Toma());
  }
}
