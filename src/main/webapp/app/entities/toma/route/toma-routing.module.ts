import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TomaComponent } from '../list/toma.component';
import { TomaDetailComponent } from '../detail/toma-detail.component';
import { TomaUpdateComponent } from '../update/toma-update.component';
import { TomaRoutingResolveService } from './toma-routing-resolve.service';

const tomaRoute: Routes = [
  {
    path: '',
    component: TomaComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TomaDetailComponent,
    resolve: {
      toma: TomaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TomaUpdateComponent,
    resolve: {
      toma: TomaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TomaUpdateComponent,
    resolve: {
      toma: TomaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tomaRoute)],
  exports: [RouterModule],
})
export class TomaRoutingModule {}
