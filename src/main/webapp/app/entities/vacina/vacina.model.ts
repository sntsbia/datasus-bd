export interface IVacina {
  id?: number;
  fabricante?: string | null;
  nomeVacina?: string | null;
}

export class Vacina implements IVacina {
  constructor(public id?: number, public fabricante?: string | null, public nomeVacina?: string | null) {}
}

export function getVacinaIdentifier(vacina: IVacina): number | undefined {
  return vacina.id;
}
