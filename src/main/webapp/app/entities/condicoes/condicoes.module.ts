import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CondicoesComponent } from './list/condicoes.component';
import { CondicoesDetailComponent } from './detail/condicoes-detail.component';
import { CondicoesUpdateComponent } from './update/condicoes-update.component';
import { CondicoesDeleteDialogComponent } from './delete/condicoes-delete-dialog.component';
import { CondicoesRoutingModule } from './route/condicoes-routing.module';

@NgModule({
  imports: [SharedModule, CondicoesRoutingModule],
  declarations: [CondicoesComponent, CondicoesDetailComponent, CondicoesUpdateComponent, CondicoesDeleteDialogComponent],
  entryComponents: [CondicoesDeleteDialogComponent],
})
export class CondicoesModule {}
