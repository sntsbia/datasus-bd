import { ITeste } from 'app/entities/teste/teste.model';
import { IMunicipio } from 'app/entities/municipio/municipio.model';

export interface IOcorre {
  id?: number;
  teste?: ITeste | null;
  municipio?: IMunicipio | null;
}

export class Ocorre implements IOcorre {
  constructor(public id?: number, public teste?: ITeste | null, public municipio?: IMunicipio | null) {}
}

export function getOcorreIdentifier(ocorre: IOcorre): number | undefined {
  return ocorre.id;
}
