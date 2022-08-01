CREATE DATABASE bd_Open_DataSus_BD1;

USE bd_Open_DataSus_BD1;

/* Modelo_Logico: */

CREATE TABLE Pessoa (
    ID INT PRIMARY KEY,
    Sexo char(1),
    Idade INT(3),
    fk_Condicoes_Condicoes_PK INT
);

CREATE TABLE Vacina (
    Fabricante VARCHAR(256),
    ID_vacina INT PRIMARY KEY,
    nome_vacina VARCHAR(256)
);

CREATE TABLE Municipio (
    id_Endereco INT PRIMARY KEY,
    Municipio VARCHAR(256),
    fk_UF_UF_PK INT
);

CREATE TABLE Teste (
    Data_Teste DATE,
    fk_Sintomas_Sintomas_PK INT,
    Resultado INT,
    ID_teste INT PRIMARY KEY,
    fk_Pessoa_ID INT
);

CREATE TABLE Condicoes (
    Condicoes_PK INT NOT NULL PRIMARY KEY,
    Condicoes VARCHAR(256)
);

CREATE TABLE UF (
    UF_PK INT NOT NULL PRIMARY KEY,
    Codigo_IBGE INT,
    Estado VARCHAR(256),
    Bandeira BLOB
);

CREATE TABLE Sintomas (
    Sintomas_PK INT NOT NULL PRIMARY KEY,
    Sintomas VARCHAR(256)
);

CREATE TABLE Toma (
    fk_Vacina_ID_vacina INT,
    fk_Pessoa_ID INT,
    Data DATE,
    Lote VARCHAR(256),
    Dose INT
);

CREATE TABLE Ocorre (
    fk_Teste_ID_teste INT,
    fk_Municipio_id_Endereco INT
);

CREATE TABLE Mora (
    fk_Pessoa_ID INT,
    fk_Municipio_id_Endereco INT
);

ALTER TABLE Pessoa ADD CONSTRAINT FK_Pessoa_2
    FOREIGN KEY (fk_Condicoes_Condicoes_PK)
    REFERENCES Condicoes (Condicoes_PK)
    ON DELETE NO ACTION;

ALTER TABLE Municipio ADD CONSTRAINT FK_Municipio_2
    FOREIGN KEY (fk_UF_UF_PK)
    REFERENCES UF (UF_PK)
    ON DELETE SET NULL;

ALTER TABLE Teste ADD CONSTRAINT FK_Teste_2
    FOREIGN KEY (fk_Sintomas_Sintomas_PK)
    REFERENCES Sintomas (Sintomas_PK)
    ON DELETE NO ACTION;

ALTER TABLE Teste ADD CONSTRAINT FK_Teste_3
    FOREIGN KEY (fk_Pessoa_ID)
    REFERENCES Pessoa (ID)
    ON DELETE SET NULL;

ALTER TABLE Toma ADD CONSTRAINT FK_Toma_1
    FOREIGN KEY (fk_Vacina_ID_vacina)
    REFERENCES Vacina (ID_vacina)
    ON DELETE SET NULL;

ALTER TABLE Toma ADD CONSTRAINT FK_Toma_2
    FOREIGN KEY (fk_Pessoa_ID)
    REFERENCES Pessoa (ID)
    ON DELETE SET NULL;

ALTER TABLE Ocorre ADD CONSTRAINT FK_Ocorre_1
    FOREIGN KEY (fk_Teste_ID_teste)
    REFERENCES Teste (ID_teste)
    ON DELETE SET NULL;

ALTER TABLE Ocorre ADD CONSTRAINT FK_Ocorre_2
    FOREIGN KEY (fk_Municipio_id_Endereco)
    REFERENCES Municipio (id_Endereco)
    ON DELETE SET NULL;

ALTER TABLE Mora ADD CONSTRAINT FK_Mora_1
    FOREIGN KEY (fk_Pessoa_ID)
    REFERENCES Pessoa (ID)
    ON DELETE SET NULL;

ALTER TABLE Mora ADD CONSTRAINT FK_Mora_2
    FOREIGN KEY (fk_Municipio_id_Endereco)
    REFERENCES Municipio (id_Endereco)
    ON DELETE SET NULL;
