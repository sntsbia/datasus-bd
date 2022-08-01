import dayjs from 'dayjs/esm';
import { IVacina } from 'app/entities/vacina/vacina.model';
import { IPessoa } from 'app/entities/pessoa/pessoa.model';

export interface IToma {
  id?: number;
  data?: dayjs.Dayjs | null;
  lote?: string | null;
  dose?: number | null;
  vacina?: IVacina | null;
  pessoa?: IPessoa | null;
}

export class Toma implements IToma {
  constructor(
    public id?: number,
    public data?: dayjs.Dayjs | null,
    public lote?: string | null,
    public dose?: number | null,
    public vacina?: IVacina | null,
    public pessoa?: IPessoa | null
  ) {}
}

export function getTomaIdentifier(toma: IToma): number | undefined {
  return toma.id;
}
