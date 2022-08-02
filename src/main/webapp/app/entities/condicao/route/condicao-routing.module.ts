import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CondicaoComponent } from '../list/condicao.component';
import { CondicaoDetailComponent } from '../detail/condicao-detail.component';
import { CondicaoUpdateComponent } from '../update/condicao-update.component';
import { CondicaoRoutingResolveService } from './condicao-routing-resolve.service';

const condicaoRoute: Routes = [
  {
    path: '',
    component: CondicaoComponent,
    data: {
      defaultSort: 'idCondicao,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idCondicao/view',
    component: CondicaoDetailComponent,
    resolve: {
      condicao: CondicaoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CondicaoUpdateComponent,
    resolve: {
      condicao: CondicaoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idCondicao/edit',
    component: CondicaoUpdateComponent,
    resolve: {
      condicao: CondicaoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(condicaoRoute)],
  exports: [RouterModule],
})
export class CondicaoRoutingModule {}
