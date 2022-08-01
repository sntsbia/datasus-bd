import { IUf } from 'app/entities/uf/uf.model';

export interface IMunicipio {
  id?: number;
  municipioNome?: string | null;
  uf?: IUf | null;
}

export class Municipio implements IMunicipio {
  constructor(public id?: number, public municipioNome?: string | null, public uf?: IUf | null) {}
}

export function getMunicipioIdentifier(municipio: IMunicipio): number | undefined {
  return municipio.id;
}
