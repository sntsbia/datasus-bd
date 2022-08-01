import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UfComponent } from '../list/uf.component';
import { UfDetailComponent } from '../detail/uf-detail.component';
import { UfUpdateComponent } from '../update/uf-update.component';
import { UfRoutingResolveService } from './uf-routing-resolve.service';

const ufRoute: Routes = [
  {
    path: '',
    component: UfComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UfDetailComponent,
    resolve: {
      uf: UfRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UfUpdateComponent,
    resolve: {
      uf: UfRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UfUpdateComponent,
    resolve: {
      uf: UfRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ufRoute)],
  exports: [RouterModule],
})
export class UfRoutingModule {}
