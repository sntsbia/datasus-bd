import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MoraComponent } from '../list/mora.component';
import { MoraDetailComponent } from '../detail/mora-detail.component';
import { MoraUpdateComponent } from '../update/mora-update.component';
import { MoraRoutingResolveService } from './mora-routing-resolve.service';

const moraRoute: Routes = [
  {
    path: '',
    component: MoraComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MoraDetailComponent,
    resolve: {
      mora: MoraRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MoraUpdateComponent,
    resolve: {
      mora: MoraRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MoraUpdateComponent,
    resolve: {
      mora: MoraRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(moraRoute)],
  exports: [RouterModule],
})
export class MoraRoutingModule {}
