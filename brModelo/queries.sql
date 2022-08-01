--Listar todos municípios com pelo menos um caso:
SELECT Municipio FROM municipio inner join ocorre on ocorre.fk_Municipio_id_Endereco = id_Endereco;

--qual a vacina mais aplicada por municipio:
SELECT municipio, nome_vacina, max(num_vac) FROM
    ( SELECT count(*) as num_vac, nome_vacina, municipio FROM
         Toma NATURAL JOIN Mora INNER JOIN Vacina ON Toma.fk_vacina_id_vacina = id_vacina
         INNER JOIN Municipio ON Mora.fk_Municipio_id_Endereco = Municipio.id_endereco GROUP BY municipio) AS cont_vac
    GROUP BY municipio;

--qual o numero de pessoas que apresentaram o sintoma 'febre':
SELECT sintomas, count(*) FROM Teste INNER JOIN Sintomas
    ON Sintomas.sintomas_pk = Teste.fk_sintomas_sintomas_pk
    WHERE Sintomas.sintomas = 'febre'
    GROUP BY sintomas;

--qual o caso mais recente por município
SELECT Data_teste, Municipio FROM Teste NATURAL JOIN Mora INNER JOIN Municipio ON Municipio.id_Endereco = Mora.fk_Municipio_id_Endereco WHERE Teste.RESULTADO = 1 GROUP BY Municipio;

--mostrar idade da pessoa e data de vacinação de todas as pessoas que tomaram coronavac
SELECT idade, data FROM Toma INNER JOIN Pessoa ON Pessoa.ID = Toma.fk_pessoa_id
    INNER JOIN Vacina ON Toma.fk_vacina_id_vacina = Vacina.id_vacina
    WHERE nome_vacina = 'SINOVAC';

--Mostrar todas as pessoas que moram no municipio Rio de Janeiro e tem idade acima de 30.
SELECT ID FROM Pessoa INNER JOIN Mora ON ID = fk_pessoa_id
INNER JOIN Municipio ON fk_municipio_id_endereco = id_endereco
WHERE idade > 30 AND municipio = 'Rio de Janeiro';

--Mostrar todas as pessoas que não tomaram nenhuma dose de Vacina (OUTER JOIN)
SELECT ID FROM Pessoa LEFT OUTER JOIN Toma ON Pessoa.ID = Toma.fk_Pessoa_ID WHERE fk_Vacina_ID_vacina IS NULL;

--Mostrar todos os municipios que houveram testes feitos
SELECT Municipio FROM Municipio INNER JOIN Ocorre ON Municipio.id_Endereco = Ocorre.fk_Municipio_id_Endereco GROUP BY Municipio;

--Listar todos os dias que aconteceu vacinação e/ou teste (usar distinct)
(SELECT distinct (Data_Teste) FROM Teste) UNION (SELECT distinct (Data) FROM Toma);


