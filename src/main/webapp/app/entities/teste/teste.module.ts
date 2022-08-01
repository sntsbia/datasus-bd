import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TesteComponent } from './list/teste.component';
import { TesteDetailComponent } from './detail/teste-detail.component';
import { TesteUpdateComponent } from './update/teste-update.component';
import { TesteDeleteDialogComponent } from './delete/teste-delete-dialog.component';
import { TesteRoutingModule } from './route/teste-routing.module';

@NgModule({
  imports: [SharedModule, TesteRoutingModule],
  declarations: [TesteComponent, TesteDetailComponent, TesteUpdateComponent, TesteDeleteDialogComponent],
  entryComponents: [TesteDeleteDialogComponent],
})
export class TesteModule {}
