import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMora, Mora } from '../mora.model';
import { MoraService } from '../service/mora.service';

@Injectable({ providedIn: 'root' })
export class MoraRoutingResolveService implements Resolve<IMora> {
  constructor(protected service: MoraService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMora> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((mora: HttpResponse<Mora>) => {
          if (mora.body) {
            return of(mora.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Mora());
  }
}
