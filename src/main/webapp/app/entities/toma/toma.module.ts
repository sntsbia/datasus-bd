import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TomaComponent } from './list/toma.component';
import { TomaDetailComponent } from './detail/toma-detail.component';
import { TomaUpdateComponent } from './update/toma-update.component';
import { TomaDeleteDialogComponent } from './delete/toma-delete-dialog.component';
import { TomaRoutingModule } from './route/toma-routing.module';

@NgModule({
  imports: [SharedModule, TomaRoutingModule],
  declarations: [TomaComponent, TomaDetailComponent, TomaUpdateComponent, TomaDeleteDialogComponent],
  entryComponents: [TomaDeleteDialogComponent],
})
export class TomaModule {}
