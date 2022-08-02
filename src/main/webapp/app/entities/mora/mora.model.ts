import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { IMunicipio } from 'app/entities/municipio/municipio.model';

export interface IMora {
  id?: number;
  fkIdPessoa?: IPessoa | null;
  fkIdMunicipio?: IMunicipio | null;
}

export class Mora implements IMora {
  constructor(public id?: number, public fkIdPessoa?: IPessoa | null, public fkIdMunicipio?: IMunicipio | null) {}
}

export function getMoraIdentifier(mora: IMora): number | undefined {
  return mora.id;
}
