import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'pessoa',
        data: { pageTitle: 'datasusBdApp.pessoa.home.title' },
        loadChildren: () => import('./pessoa/pessoa.module').then(m => m.PessoaModule),
      },
      {
        path: 'condicoes',
        data: { pageTitle: 'datasusBdApp.condicoes.home.title' },
        loadChildren: () => import('./condicoes/condicoes.module').then(m => m.CondicoesModule),
      },
      {
        path: 'mora',
        data: { pageTitle: 'datasusBdApp.mora.home.title' },
        loadChildren: () => import('./mora/mora.module').then(m => m.MoraModule),
      },
      {
        path: 'vacina',
        data: { pageTitle: 'datasusBdApp.vacina.home.title' },
        loadChildren: () => import('./vacina/vacina.module').then(m => m.VacinaModule),
      },
      {
        path: 'municipio',
        data: { pageTitle: 'datasusBdApp.municipio.home.title' },
        loadChildren: () => import('./municipio/municipio.module').then(m => m.MunicipioModule),
      },
      {
        path: 'teste',
        data: { pageTitle: 'datasusBdApp.teste.home.title' },
        loadChildren: () => import('./teste/teste.module').then(m => m.TesteModule),
      },
      {
        path: 'uf',
        data: { pageTitle: 'datasusBdApp.uf.home.title' },
        loadChildren: () => import('./uf/uf.module').then(m => m.UfModule),
      },
      {
        path: 'sintomas',
        data: { pageTitle: 'datasusBdApp.sintomas.home.title' },
        loadChildren: () => import('./sintomas/sintomas.module').then(m => m.SintomasModule),
      },
      {
        path: 'toma',
        data: { pageTitle: 'datasusBdApp.toma.home.title' },
        loadChildren: () => import('./toma/toma.module').then(m => m.TomaModule),
      },
      {
        path: 'ocorre',
        data: { pageTitle: 'datasusBdApp.ocorre.home.title' },
        loadChildren: () => import('./ocorre/ocorre.module').then(m => m.OcorreModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
