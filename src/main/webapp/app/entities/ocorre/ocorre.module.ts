import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OcorreComponent } from './list/ocorre.component';
import { OcorreDetailComponent } from './detail/ocorre-detail.component';
import { OcorreUpdateComponent } from './update/ocorre-update.component';
import { OcorreDeleteDialogComponent } from './delete/ocorre-delete-dialog.component';
import { OcorreRoutingModule } from './route/ocorre-routing.module';

@NgModule({
  imports: [SharedModule, OcorreRoutingModule],
  declarations: [OcorreComponent, OcorreDetailComponent, OcorreUpdateComponent, OcorreDeleteDialogComponent],
  entryComponents: [OcorreDeleteDialogComponent],
})
export class OcorreModule {}
