import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OcorreComponent } from '../list/ocorre.component';
import { OcorreDetailComponent } from '../detail/ocorre-detail.component';
import { OcorreUpdateComponent } from '../update/ocorre-update.component';
import { OcorreRoutingResolveService } from './ocorre-routing-resolve.service';

const ocorreRoute: Routes = [
  {
    path: '',
    component: OcorreComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OcorreDetailComponent,
    resolve: {
      ocorre: OcorreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OcorreUpdateComponent,
    resolve: {
      ocorre: OcorreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OcorreUpdateComponent,
    resolve: {
      ocorre: OcorreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ocorreRoute)],
  exports: [RouterModule],
})
export class OcorreRoutingModule {}
