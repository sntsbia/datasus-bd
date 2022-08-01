import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SintomasComponent } from '../list/sintomas.component';
import { SintomasDetailComponent } from '../detail/sintomas-detail.component';
import { SintomasUpdateComponent } from '../update/sintomas-update.component';
import { SintomasRoutingResolveService } from './sintomas-routing-resolve.service';

const sintomasRoute: Routes = [
  {
    path: '',
    component: SintomasComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SintomasDetailComponent,
    resolve: {
      sintomas: SintomasRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SintomasUpdateComponent,
    resolve: {
      sintomas: SintomasRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SintomasUpdateComponent,
    resolve: {
      sintomas: SintomasRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(sintomasRoute)],
  exports: [RouterModule],
})
export class SintomasRoutingModule {}
