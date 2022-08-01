import { ICondicoes } from 'app/entities/condicoes/condicoes.model';
import { Sexo } from 'app/entities/enumerations/sexo.model';

export interface IPessoa {
  id?: number;
  sexo?: Sexo | null;
  idade?: number | null;
  condicoes?: ICondicoes[] | null;
}

export class Pessoa implements IPessoa {
  constructor(public id?: number, public sexo?: Sexo | null, public idade?: number | null, public condicoes?: ICondicoes[] | null) {}
}

export function getPessoaIdentifier(pessoa: IPessoa): number | undefined {
  return pessoa.id;
}
