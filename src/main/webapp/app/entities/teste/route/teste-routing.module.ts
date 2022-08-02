import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TesteComponent } from '../list/teste.component';
import { TesteDetailComponent } from '../detail/teste-detail.component';
import { TesteUpdateComponent } from '../update/teste-update.component';
import { TesteRoutingResolveService } from './teste-routing-resolve.service';

const testeRoute: Routes = [
  {
    path: '',
    component: TesteComponent,
    data: {
      defaultSort: 'idTeste,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idTeste/view',
    component: TesteDetailComponent,
    resolve: {
      teste: TesteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TesteUpdateComponent,
    resolve: {
      teste: TesteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idTeste/edit',
    component: TesteUpdateComponent,
    resolve: {
      teste: TesteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(testeRoute)],
  exports: [RouterModule],
})
export class TesteRoutingModule {}
