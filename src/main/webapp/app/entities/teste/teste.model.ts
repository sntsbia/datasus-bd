import dayjs from 'dayjs/esm';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { ISintomas } from 'app/entities/sintomas/sintomas.model';

export interface ITeste {
  idTeste?: number;
  dataTeste?: dayjs.Dayjs | null;
  resultado?: number | null;
  fkIdTeste?: IPessoa | null;
  sintomas?: ISintomas | null;
}

export class Teste implements ITeste {
  constructor(
    public idTeste?: number,
    public dataTeste?: dayjs.Dayjs | null,
    public resultado?: number | null,
    public fkIdTeste?: IPessoa | null,
    public sintomas?: ISintomas | null
  ) {}
}

export function getTesteIdentifier(teste: ITeste): number | undefined {
  return teste.idTeste;
}
