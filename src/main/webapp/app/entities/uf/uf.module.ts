import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UfComponent } from './list/uf.component';
import { UfDetailComponent } from './detail/uf-detail.component';
import { UfUpdateComponent } from './update/uf-update.component';
import { UfDeleteDialogComponent } from './delete/uf-delete-dialog.component';
import { UfRoutingModule } from './route/uf-routing.module';

@NgModule({
  imports: [SharedModule, UfRoutingModule],
  declarations: [UfComponent, UfDetailComponent, UfUpdateComponent, UfDeleteDialogComponent],
  entryComponents: [UfDeleteDialogComponent],
})
export class UfModule {}
