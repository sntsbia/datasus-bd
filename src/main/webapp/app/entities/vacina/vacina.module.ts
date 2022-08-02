import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { VacinaComponent } from './list/vacina.component';
import { VacinaDetailComponent } from './detail/vacina-detail.component';
import { VacinaUpdateComponent } from './update/vacina-update.component';
import { VacinaDeleteDialogComponent } from './delete/vacina-delete-dialog.component';
import { VacinaRoutingModule } from './route/vacina-routing.module';

@NgModule({
  imports: [SharedModule, VacinaRoutingModule],
  declarations: [VacinaComponent, VacinaDetailComponent, VacinaUpdateComponent, VacinaDeleteDialogComponent],
  entryComponents: [VacinaDeleteDialogComponent],
})
export class VacinaModule {}
