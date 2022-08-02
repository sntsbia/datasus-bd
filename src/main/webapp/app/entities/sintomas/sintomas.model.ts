import { ITeste } from 'app/entities/teste/teste.model';

export interface ISintomas {
  idSintomas?: number;
  sintomas?: string | null;
  fkIdTestes?: ITeste[] | null;
}

export class Sintomas implements ISintomas {
  constructor(public idSintomas?: number, public sintomas?: string | null, public fkIdTestes?: ITeste[] | null) {}
}

export function getSintomasIdentifier(sintomas: ISintomas): number | undefined {
  return sintomas.idSintomas;
}
