CREATE DATABASE bd_Open_DataSus_BD1;

USE bd_Open_DataSus_BD1; 

CREATE TABLE Pessoa (
  id_Pessoa int PRIMARY KEY,
  sexo varchar(1),
  idade int
);

CREATE TABLE Condicao (
  id_Condicao int PRIMARY KEY,
  condicao varchar(256),
  fk_id_Pessoa int
);

CREATE TABLE Vacina (
  id_Vacina int PRIMARY KEY,
  fabricante varchar(256),
  nome_Vacina varchar(256)
);

CREATE TABLE Municipio (
  id_Municipio int PRIMARY KEY,
  municipio varchar(256),
  fk_id_Uf int
);

CREATE TABLE Teste (
  id_Teste int PRIMARY KEY,
  data_Teste DATE,
  resultado int,
  fk_id_Pessoa int
);

CREATE TABLE Uf (
  id_Uf int NOT NULL PRIMARY KEY,
  codigo_ibge int,
  estado varchar(256),
  bandeira BLOB
);

CREATE TABLE Sintomas (
  id_Sintomas int NOT NULL PRIMARY KEY,
  sintomas varchar(256),
  fK_id_Teste int
);

CREATE TABLE Toma (
  fk_id_Vacina int,
  fk_id_Pessoa int,
  data DATE,
  lote varchar(256),
  dose int
);

CREATE TABLE Ocorre (
  fK_id_Teste int,
  fk_id_Municipio int
);

CREATE TABLE Mora (
  fk_id_Pessoa int,
  fk_id_Municipio int
);

ALTER TABLE Municipio ADD CONSTRAINT fk_Municipio_Uf
  FOREIGN KEY (fk_id_Uf)
  REFERENCES Uf(id_Uf)
  ON DELETE SET NULL;

ALTER TABLE Sintomas ADD CONSTRAINT fk_Sintomas_Teste
  FOREIGN KEY (fk_id_Teste)
  REFERENCES Teste(id_Teste)
  ON DELETE NO ACTION;

ALTER TABLE Condicao ADD CONSTRAINT fk_Condicao_Pessoa
  FOREIGN KEY (fk_id_Pessoa)
  REFERENCES Pessoa(id_Pessoa)
  ON DELETE NO ACTION;

ALTER TABLE Teste ADD CONSTRAINT fk_Teste_Pessoa
  FOREIGN KEY (fk_id_Pessoa)
  REFERENCES Pessoa(id_Pessoa)
  ON DELETE SET NULL;

ALTER TABLE Toma ADD CONSTRAINT fk_Toma_Vacina
  FOREIGN KEY (fk_id_Vacina)
  REFERENCES Vacina(id_Vacina)
  ON DELETE SET NULL;

ALTER TABLE Toma ADD CONSTRAINT fk_Toma_Pessoa
  FOREIGN KEY (fk_id_Pessoa)
  REFERENCES Pessoa(id_Pessoa)
  ON DELETE SET NULL;

ALTER TABLE Ocorre ADD CONSTRAINT fk_Ocorre_Teste
  FOREIGN KEY (fk_id_Teste)
  REFERENCES Teste(id_Teste)
  ON DELETE SET NULL;

ALTER TABLE Ocorre ADD CONSTRAINT fk_Ocorre_Municipio
    FOREIGN KEY (fk_id_Municipio)
    REFERENCES Municipio(id_Municipio)
    ON DELETE SET NULL;

ALTER TABLE Mora ADD CONSTRAINT fk_Mora_Pessoa
      FOREIGN KEY (fk_id_Pessoa)
      REFERENCES Pessoa(id_Pessoa)
      ON DELETE SET NULL;

      
ALTER TABLE Mora ADD CONSTRAINT fk_Mora_Municipio
      FOREIGN KEY (fk_id_Municipio)
      REFERENCES Municipio(id_Municipio)
      ON DELETE SET NULL;




  
