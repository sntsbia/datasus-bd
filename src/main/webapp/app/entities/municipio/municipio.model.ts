import { IUf } from 'app/entities/uf/uf.model';

export interface IMunicipio {
  idMunicipio?: number;
  municipio?: string | null;
  fkIdUf?: IUf | null;
}

export class Municipio implements IMunicipio {
  constructor(public idMunicipio?: number, public municipio?: string | null, public fkIdUf?: IUf | null) {}
}

export function getMunicipioIdentifier(municipio: IMunicipio): number | undefined {
  return municipio.idMunicipio;
}
