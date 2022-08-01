import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MunicipioComponent } from './list/municipio.component';
import { MunicipioDetailComponent } from './detail/municipio-detail.component';
import { MunicipioUpdateComponent } from './update/municipio-update.component';
import { MunicipioDeleteDialogComponent } from './delete/municipio-delete-dialog.component';
import { MunicipioRoutingModule } from './route/municipio-routing.module';

@NgModule({
  imports: [SharedModule, MunicipioRoutingModule],
  declarations: [MunicipioComponent, MunicipioDetailComponent, MunicipioUpdateComponent, MunicipioDeleteDialogComponent],
  entryComponents: [MunicipioDeleteDialogComponent],
})
export class MunicipioModule {}
