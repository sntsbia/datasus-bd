import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SintomasComponent } from './list/sintomas.component';
import { SintomasDetailComponent } from './detail/sintomas-detail.component';
import { SintomasUpdateComponent } from './update/sintomas-update.component';
import { SintomasDeleteDialogComponent } from './delete/sintomas-delete-dialog.component';
import { SintomasRoutingModule } from './route/sintomas-routing.module';

@NgModule({
  imports: [SharedModule, SintomasRoutingModule],
  declarations: [SintomasComponent, SintomasDetailComponent, SintomasUpdateComponent, SintomasDeleteDialogComponent],
  entryComponents: [SintomasDeleteDialogComponent],
})
export class SintomasModule {}
