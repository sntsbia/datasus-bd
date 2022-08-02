import { ITeste } from 'app/entities/teste/teste.model';
import { IMunicipio } from 'app/entities/municipio/municipio.model';

export interface IOcorre {
  id?: number;
  fkIdTeste?: ITeste | null;
  fkIdMunicipio?: IMunicipio | null;
}

export class Ocorre implements IOcorre {
  constructor(public id?: number, public fkIdTeste?: ITeste | null, public fkIdMunicipio?: IMunicipio | null) {}
}

export function getOcorreIdentifier(ocorre: IOcorre): number | undefined {
  return ocorre.id;
}
