import { ICondicao } from 'app/entities/condicao/condicao.model';
import { Sexo } from 'app/entities/enumerations/sexo.model';

export interface IPessoa {
  idPessoa?: number;
  sexo?: Sexo | null;
  idade?: number | null;
  condicao?: ICondicao | null;
}

export class Pessoa implements IPessoa {
  constructor(public idPessoa?: number, public sexo?: Sexo | null, public idade?: number | null, public condicao?: ICondicao | null) {}
}

export function getPessoaIdentifier(pessoa: IPessoa): number | undefined {
  return pessoa.idPessoa;
}
