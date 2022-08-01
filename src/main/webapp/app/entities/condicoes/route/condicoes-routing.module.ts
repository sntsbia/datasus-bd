import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CondicoesComponent } from '../list/condicoes.component';
import { CondicoesDetailComponent } from '../detail/condicoes-detail.component';
import { CondicoesUpdateComponent } from '../update/condicoes-update.component';
import { CondicoesRoutingResolveService } from './condicoes-routing-resolve.service';

const condicoesRoute: Routes = [
  {
    path: '',
    component: CondicoesComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CondicoesDetailComponent,
    resolve: {
      condicoes: CondicoesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CondicoesUpdateComponent,
    resolve: {
      condicoes: CondicoesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CondicoesUpdateComponent,
    resolve: {
      condicoes: CondicoesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(condicoesRoute)],
  exports: [RouterModule],
})
export class CondicoesRoutingModule {}
