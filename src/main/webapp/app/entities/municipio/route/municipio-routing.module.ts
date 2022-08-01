import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MunicipioComponent } from '../list/municipio.component';
import { MunicipioDetailComponent } from '../detail/municipio-detail.component';
import { MunicipioUpdateComponent } from '../update/municipio-update.component';
import { MunicipioRoutingResolveService } from './municipio-routing-resolve.service';

const municipioRoute: Routes = [
  {
    path: '',
    component: MunicipioComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MunicipioDetailComponent,
    resolve: {
      municipio: MunicipioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MunicipioUpdateComponent,
    resolve: {
      municipio: MunicipioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MunicipioUpdateComponent,
    resolve: {
      municipio: MunicipioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(municipioRoute)],
  exports: [RouterModule],
})
export class MunicipioRoutingModule {}
