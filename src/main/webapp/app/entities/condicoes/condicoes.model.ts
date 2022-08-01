import { IPessoa } from 'app/entities/pessoa/pessoa.model';

export interface ICondicoes {
  id?: number;
  condicao?: string | null;
  pessoas?: IPessoa[] | null;
}

export class Condicoes implements ICondicoes {
  constructor(public id?: number, public condicao?: string | null, public pessoas?: IPessoa[] | null) {}
}

export function getCondicoesIdentifier(condicoes: ICondicoes): number | undefined {
  return condicoes.id;
}
