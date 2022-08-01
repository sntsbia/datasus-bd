import dayjs from 'dayjs/esm';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';
import { IMunicipio } from 'app/entities/municipio/municipio.model';
import { ISintomas } from 'app/entities/sintomas/sintomas.model';

export interface ITeste {
  id?: number;
  dataTeste?: dayjs.Dayjs | null;
  resultado?: number | null;
  pessoa?: IPessoa | null;
  municipio?: IMunicipio | null;
  sintomas?: ISintomas[] | null;
}

export class Teste implements ITeste {
  constructor(
    public id?: number,
    public dataTeste?: dayjs.Dayjs | null,
    public resultado?: number | null,
    public pessoa?: IPessoa | null,
    public municipio?: IMunicipio | null,
    public sintomas?: ISintomas[] | null
  ) {}
}

export function getTesteIdentifier(teste: ITeste): number | undefined {
  return teste.id;
}
