import { IPessoa } from 'app/entities/pessoa/pessoa.model';

export interface ICondicao {
  idCondicao?: number;
  condicao?: string | null;
  fkIdPessoas?: IPessoa[] | null;
}

export class Condicao implements ICondicao {
  constructor(public idCondicao?: number, public condicao?: string | null, public fkIdPessoas?: IPessoa[] | null) {}
}

export function getCondicaoIdentifier(condicao: ICondicao): number | undefined {
  return condicao.idCondicao;
}
