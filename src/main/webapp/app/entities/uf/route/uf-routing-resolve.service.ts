import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUf, Uf } from '../uf.model';
import { UfService } from '../service/uf.service';

@Injectable({ providedIn: 'root' })
export class UfRoutingResolveService implements Resolve<IUf> {
  constructor(protected service: UfService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUf> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((uf: HttpResponse<Uf>) => {
          if (uf.body) {
            return of(uf.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Uf());
  }
}
