import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CondicaoComponent } from './list/condicao.component';
import { CondicaoDetailComponent } from './detail/condicao-detail.component';
import { CondicaoUpdateComponent } from './update/condicao-update.component';
import { CondicaoDeleteDialogComponent } from './delete/condicao-delete-dialog.component';
import { CondicaoRoutingModule } from './route/condicao-routing.module';

@NgModule({
  imports: [SharedModule, CondicaoRoutingModule],
  declarations: [CondicaoComponent, CondicaoDetailComponent, CondicaoUpdateComponent, CondicaoDeleteDialogComponent],
  entryComponents: [CondicaoDeleteDialogComponent],
})
export class CondicaoModule {}
