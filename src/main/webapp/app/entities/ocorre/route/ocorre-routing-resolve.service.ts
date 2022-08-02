import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOcorre, Ocorre } from '../ocorre.model';
import { OcorreService } from '../service/ocorre.service';

@Injectable({ providedIn: 'root' })
export class OcorreRoutingResolveService implements Resolve<IOcorre> {
  constructor(protected service: OcorreService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOcorre> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ocorre: HttpResponse<Ocorre>) => {
          if (ocorre.body) {
            return of(ocorre.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ocorre());
  }
}
