--INSERT INTO TBAREACONHECIMENTO(ID, DESCRICAO, VERSION) VALUES (1, 'Ciências agrárias','0')
--INSERT INTO TBAREACONHECIMENTO(ID, DESCRICAO, VERSION) VALUES (2, 'Ciências biológicas','0')
--INSERT INTO TBAREACONHECIMENTO(ID, DESCRICAO, VERSION) VALUES (3, 'Ciências da saúde','0')
--INSERT INTO TBAREACONHECIMENTO(ID, DESCRICAO, VERSION) VALUES (4, 'Ciências exatas e da terra','0')
--INSERT INTO TBAREACONHECIMENTO(ID, DESCRICAO, VERSION) VALUES (5, 'Ciências humanas','0')
--INSERT INTO TBAREACONHECIMENTO(ID, DESCRICAO, VERSION) VALUES (6, 'Ciências sociais aplicadas','0')
--INSERT INTO TBAREACONHECIMENTO(ID, DESCRICAO, VERSION) VALUES (7, 'Engenharias','0')
--INSERT INTO TBAREACONHECIMENTO(ID, DESCRICAO, VERSION) VALUES (8, 'Linguística, letras e artes','0')
INSERT INTO TBCURSO (ID, NOME, CODIGO, DESCRICAO, MODALIDADE, VERSION) VALUES (1, 'MEDICINA', 'MED', 'Curso de Medicina', 1, 0)
INSERT INTO TBCURSO (ID, NOME, CODIGO, DESCRICAO, MODALIDADE, VERSION) VALUES (2, 'LETRAS', 'LET', 'Curso de Letras', 2, 0)
INSERT INTO TBCURSO (ID, NOME, CODIGO, DESCRICAO, MODALIDADE, VERSION) VALUES (3, 'GEOGRAFIA', 'GEO', 'Curso de Geografia', 2, 0)
INSERT INTO TBCURSO (ID, NOME, CODIGO, DESCRICAO, MODALIDADE, VERSION) VALUES (4, 'ENGENHARIA CIVIL', 'ENC', 'Curso de Engenharia Civil', 1, 0)

INSERT INTO TBCURRICULO (ID, ANO, SEMESTRE, FKCURSO, VERSION) VALUES (1, 2010, 1, 1, 0)
INSERT INTO TBCURRICULO (ID, ANO, SEMESTRE, FKCURSO, VERSION) VALUES (2, 2017, 2, 1, 0)
INSERT INTO TBCURRICULO (ID, ANO, SEMESTRE, FKCURSO, VERSION) VALUES (3, 2010, 2, 4, 0)
INSERT INTO TBCURRICULO (ID, ANO, SEMESTRE, FKCURSO, VERSION) VALUES (4, 2009, 1, 2, 0)