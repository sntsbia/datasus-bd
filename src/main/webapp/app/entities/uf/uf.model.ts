export interface IUf {
  idUf?: number;
  codigoIbge?: number | null;
  estado?: string | null;
  bandeiraContentType?: string | null;
  bandeira?: string | null;
}

export class Uf implements IUf {
  constructor(
    public idUf?: number,
    public codigoIbge?: number | null,
    public estado?: string | null,
    public bandeiraContentType?: string | null,
    public bandeira?: string | null
  ) {}
}

export function getUfIdentifier(uf: IUf): number | undefined {
  return uf.idUf;
}
