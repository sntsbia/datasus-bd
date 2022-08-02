import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MoraComponent } from './list/mora.component';
import { MoraDetailComponent } from './detail/mora-detail.component';
import { MoraUpdateComponent } from './update/mora-update.component';
import { MoraDeleteDialogComponent } from './delete/mora-delete-dialog.component';
import { MoraRoutingModule } from './route/mora-routing.module';

@NgModule({
  imports: [SharedModule, MoraRoutingModule],
  declarations: [MoraComponent, MoraDetailComponent, MoraUpdateComponent, MoraDeleteDialogComponent],
  entryComponents: [MoraDeleteDialogComponent],
})
export class MoraModule {}
