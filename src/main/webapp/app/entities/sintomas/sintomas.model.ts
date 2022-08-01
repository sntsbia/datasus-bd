import { ITeste } from 'app/entities/teste/teste.model';

export interface ISintomas {
  id?: number;
  descricaoSintoma?: string | null;
  testes?: ITeste[] | null;
}

export class Sintomas implements ISintomas {
  constructor(public id?: number, public descricaoSintoma?: string | null, public testes?: ITeste[] | null) {}
}

export function getSintomasIdentifier(sintomas: ISintomas): number | undefined {
  return sintomas.id;
}
