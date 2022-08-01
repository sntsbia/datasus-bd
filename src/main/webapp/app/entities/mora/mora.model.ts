import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { IMunicipio } from 'app/entities/municipio/municipio.model';

export interface IMora {
  id?: number;
  pessoa?: IPessoa | null;
  municipio?: IMunicipio | null;
}

export class Mora implements IMora {
  constructor(public id?: number, public pessoa?: IPessoa | null, public municipio?: IMunicipio | null) {}
}

export function getMoraIdentifier(mora: IMora): number | undefined {
  return mora.id;
}
