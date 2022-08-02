import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { VacinaComponent } from '../list/vacina.component';
import { VacinaDetailComponent } from '../detail/vacina-detail.component';
import { VacinaUpdateComponent } from '../update/vacina-update.component';
import { VacinaRoutingResolveService } from './vacina-routing-resolve.service';

const vacinaRoute: Routes = [
  {
    path: '',
    component: VacinaComponent,
    data: {
      defaultSort: 'idVacina,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idVacina/view',
    component: VacinaDetailComponent,
    resolve: {
      vacina: VacinaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VacinaUpdateComponent,
    resolve: {
      vacina: VacinaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':idVacina/edit',
    component: VacinaUpdateComponent,
    resolve: {
      vacina: VacinaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(vacinaRoute)],
  exports: [RouterModule],
})
export class VacinaRoutingModule {}
