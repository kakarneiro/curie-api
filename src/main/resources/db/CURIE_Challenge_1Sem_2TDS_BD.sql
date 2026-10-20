-- =====================================================================
--  PROJETO CURIE - "Um laboratorio nuclear em cada escola por R$ 300"
--  Challenge FIAP 2026 - CTMSP / Marinha do Brasil - 1o Semestre
--  Disciplina: Mastering Relational and Non-Relational Database
--  Grupo CURIE - Turma 2TDSPB
--    Matheus Carneiro Maciel      RM 567753
--    Murilo Marques               RM 568224
--    Paulo Henrique da Silva Kian RM 563343
--
--  Conteudo: 1) Limpeza  2) DDL (sequences, tabelas, constraints)
--            3) DML (5+ registros por tabela)  4) Consultas com JOIN
--  Banco alvo: Oracle Database (testado para Oracle 19c / FIAP)
--  Execucao: rodar o script inteiro no SQL Developer (F5) ou SQL*Plus.
-- =====================================================================

SET DEFINE OFF;

-- =====================================================================
-- 1) LIMPEZA (permite reexecutar o script do inicio ao fim sem erros)
-- =====================================================================
BEGIN
  FOR obj IN (
    SELECT 'TABLE' tipo, table_name nome FROM user_tables
     WHERE table_name IN ('T_CURIE_MEDICAO','T_CURIE_KIT','T_CURIE_USUARIO',
                          'T_CURIE_EXPERIMENTO','T_CURIE_ESCOLA')
    UNION ALL
    SELECT 'SEQUENCE', sequence_name FROM user_sequences
     WHERE sequence_name IN ('SQ_CURIE_ESCOLA','SQ_CURIE_USUARIO','SQ_CURIE_KIT',
                             'SQ_CURIE_EXPERIMENTO','SQ_CURIE_MEDICAO')
    ORDER BY 1 DESC
  ) LOOP
    IF obj.tipo = 'TABLE' THEN
      EXECUTE IMMEDIATE 'DROP TABLE ' || obj.nome || ' CASCADE CONSTRAINTS PURGE';
    ELSE
      EXECUTE IMMEDIATE 'DROP SEQUENCE ' || obj.nome;
    END IF;
  END LOOP;
END;
/

-- =====================================================================
-- 2) DDL
-- =====================================================================

-- 2.1 Sequences --------------------------------------------------------
CREATE SEQUENCE SQ_CURIE_ESCOLA      START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_CURIE_USUARIO     START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_CURIE_KIT         START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_CURIE_EXPERIMENTO START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SQ_CURIE_MEDICAO     START WITH 1 INCREMENT BY 1 NOCACHE;

-- 2.2 T_CURIE_ESCOLA ---------------------------------------------------
-- Instituicao de ensino que adota o kit CURIE.
CREATE TABLE T_CURIE_ESCOLA (
  id_escola    NUMBER(10)     NOT NULL,
  nome         VARCHAR2(120)  NOT NULL,
  cidade       VARCHAR2(80)   NOT NULL,
  uf           CHAR(2)        NOT NULL,
  tipo         VARCHAR2(10)   NOT NULL,
  codigo_inep  CHAR(8),
  dt_cadastro  DATE           DEFAULT SYSDATE NOT NULL,
  CONSTRAINT PK_CURIE_ESCOLA      PRIMARY KEY (id_escola),
  CONSTRAINT UK_CURIE_ESCOLA_INEP UNIQUE (codigo_inep),
  CONSTRAINT CK_CURIE_ESCOLA_UF   CHECK (uf = UPPER(uf) AND LENGTH(uf) = 2),
  CONSTRAINT CK_CURIE_ESCOLA_TIPO CHECK (tipo IN ('PUBLICA','PRIVADA'))
);

COMMENT ON TABLE  T_CURIE_ESCOLA             IS 'Escolas participantes do Projeto CURIE';
COMMENT ON COLUMN T_CURIE_ESCOLA.codigo_inep IS 'Codigo INEP da escola (8 digitos), opcional';
COMMENT ON COLUMN T_CURIE_ESCOLA.tipo        IS 'PUBLICA ou PRIVADA';

-- 2.3 T_CURIE_USUARIO --------------------------------------------------
-- Aluno ou professor vinculado a uma escola. Acumula pontos no app.
CREATE TABLE T_CURIE_USUARIO (
  id_usuario   NUMBER(10)     NOT NULL,
  id_escola    NUMBER(10)     NOT NULL,
  nome         VARCHAR2(120)  NOT NULL,
  email        VARCHAR2(150)  NOT NULL,
  perfil       VARCHAR2(10)   NOT NULL,
  pontos       NUMBER(8)      DEFAULT 0 NOT NULL,
  dt_cadastro  DATE           DEFAULT SYSDATE NOT NULL,
  CONSTRAINT PK_CURIE_USUARIO        PRIMARY KEY (id_usuario),
  CONSTRAINT UK_CURIE_USUARIO_EMAIL  UNIQUE (email),
  CONSTRAINT FK_CURIE_USUARIO_ESCOLA FOREIGN KEY (id_escola)
             REFERENCES T_CURIE_ESCOLA (id_escola),
  CONSTRAINT CK_CURIE_USUARIO_PERFIL CHECK (perfil IN ('ALUNO','PROFESSOR')),
  CONSTRAINT CK_CURIE_USUARIO_PONTOS CHECK (pontos >= 0),
  CONSTRAINT CK_CURIE_USUARIO_EMAIL  CHECK (email LIKE '%_@_%._%')
);

COMMENT ON TABLE  T_CURIE_USUARIO        IS 'Usuarios do app CURIE (alunos e professores)';
COMMENT ON COLUMN T_CURIE_USUARIO.pontos IS 'Pontuacao acumulada nas missoes e quizzes';

-- 2.4 T_CURIE_KIT ------------------------------------------------------
-- Dispositivo fisico (ESP32 + Geiger-Muller + NFC) instalado em uma escola.
CREATE TABLE T_CURIE_KIT (
  id_kit       NUMBER(10)     NOT NULL,
  id_escola    NUMBER(10)     NOT NULL,
  cod_serial   VARCHAR2(20)   NOT NULL,
  status       VARCHAR2(10)   DEFAULT 'INATIVO' NOT NULL,
  dt_ativacao  DATE,
  CONSTRAINT PK_CURIE_KIT          PRIMARY KEY (id_kit),
  CONSTRAINT UK_CURIE_KIT_SERIAL   UNIQUE (cod_serial),
  CONSTRAINT FK_CURIE_KIT_ESCOLA   FOREIGN KEY (id_escola)
             REFERENCES T_CURIE_ESCOLA (id_escola),
  CONSTRAINT CK_CURIE_KIT_STATUS   CHECK (status IN ('ATIVO','INATIVO','MANUTENCAO')),
  CONSTRAINT CK_CURIE_KIT_ATIVACAO CHECK (status <> 'ATIVO' OR dt_ativacao IS NOT NULL)
);

COMMENT ON TABLE  T_CURIE_KIT            IS 'Kits CURIE (hardware) por escola';
COMMENT ON COLUMN T_CURIE_KIT.cod_serial IS 'Serial gravado no ESP32, ex.: CURIE-2026-0001';

-- 2.5 T_CURIE_EXPERIMENTO ---------------------------------------------
-- Catalogo de missoes: objeto medido, faixa esperada e explicacao.
-- Entidade gerenciada pela aplicacao console em .NET.
CREATE TABLE T_CURIE_EXPERIMENTO (
  id_experimento  NUMBER(10)     NOT NULL,
  nome            VARCHAR2(100)  NOT NULL,
  objeto          VARCHAR2(80)   NOT NULL,
  categoria       VARCHAR2(20)   NOT NULL,
  cpm_min         NUMBER(8,2)    NOT NULL,
  cpm_max         NUMBER(8,2)    NOT NULL,
  nivel           NUMBER(1)      NOT NULL,
  explicacao      VARCHAR2(600)  NOT NULL,
  publicado       CHAR(1)        DEFAULT 'N' NOT NULL,
  CONSTRAINT PK_CURIE_EXPERIMENTO        PRIMARY KEY (id_experimento),
  CONSTRAINT UK_CURIE_EXPERIMENTO_NOME   UNIQUE (nome),
  CONSTRAINT CK_CURIE_EXPERIMENTO_CAT    CHECK (categoria IN
             ('ALIMENTO','MINERAL','DOMESTICO','AMBIENTE','SAUDE','INDUSTRIA')),
  CONSTRAINT CK_CURIE_EXPERIMENTO_FAIXA  CHECK (cpm_min >= 0 AND cpm_max >= cpm_min),
  CONSTRAINT CK_CURIE_EXPERIMENTO_NIVEL  CHECK (nivel BETWEEN 1 AND 3),
  CONSTRAINT CK_CURIE_EXPERIMENTO_PUB    CHECK (publicado IN ('S','N'))
);

COMMENT ON TABLE  T_CURIE_EXPERIMENTO          IS 'Catalogo de experimentos/missoes do kit CURIE';
COMMENT ON COLUMN T_CURIE_EXPERIMENTO.cpm_min  IS 'Contagens por minuto minimas esperadas';
COMMENT ON COLUMN T_CURIE_EXPERIMENTO.cpm_max  IS 'Contagens por minuto maximas esperadas';
COMMENT ON COLUMN T_CURIE_EXPERIMENTO.nivel    IS '1 = basico, 2 = intermediario, 3 = avancado';

-- 2.6 T_CURIE_MEDICAO --------------------------------------------------
-- Leitura do Geiger enviada pelo kit. Alimenta o mapa colaborativo.
CREATE TABLE T_CURIE_MEDICAO (
  id_medicao      NUMBER(12)     NOT NULL,
  id_kit          NUMBER(10)     NOT NULL,
  id_usuario      NUMBER(10)     NOT NULL,
  id_experimento  NUMBER(10)     NOT NULL,
  valor_cpm       NUMBER(8,2)    NOT NULL,
  dt_hora         TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
  observacao      VARCHAR2(200),
  CONSTRAINT PK_CURIE_MEDICAO          PRIMARY KEY (id_medicao),
  CONSTRAINT FK_CURIE_MEDICAO_KIT      FOREIGN KEY (id_kit)
             REFERENCES T_CURIE_KIT (id_kit),
  CONSTRAINT FK_CURIE_MEDICAO_USUARIO  FOREIGN KEY (id_usuario)
             REFERENCES T_CURIE_USUARIO (id_usuario),
  CONSTRAINT FK_CURIE_MEDICAO_EXPER    FOREIGN KEY (id_experimento)
             REFERENCES T_CURIE_EXPERIMENTO (id_experimento),
  CONSTRAINT CK_CURIE_MEDICAO_VALOR    CHECK (valor_cpm >= 0)
);

COMMENT ON TABLE  T_CURIE_MEDICAO           IS 'Medicoes de radiacao realizadas com o kit';
COMMENT ON COLUMN T_CURIE_MEDICAO.valor_cpm IS 'Contagens por minuto lidas no tubo Geiger-Muller';

-- 2.7 Indices de apoio as consultas mais frequentes --------------------
CREATE INDEX IX_CURIE_USUARIO_ESCOLA ON T_CURIE_USUARIO (id_escola);
CREATE INDEX IX_CURIE_KIT_ESCOLA     ON T_CURIE_KIT (id_escola);
CREATE INDEX IX_CURIE_MEDICAO_KIT    ON T_CURIE_MEDICAO (id_kit);
CREATE INDEX IX_CURIE_MEDICAO_USU    ON T_CURIE_MEDICAO (id_usuario);
CREATE INDEX IX_CURIE_MEDICAO_EXP    ON T_CURIE_MEDICAO (id_experimento);
CREATE INDEX IX_CURIE_MEDICAO_DATA   ON T_CURIE_MEDICAO (dt_hora);

-- =====================================================================
-- 3) DML - CARGA INICIAL (minimo de 5 registros por tabela)
-- =====================================================================

-- 3.1 Escolas ----------------------------------------------------------
INSERT INTO T_CURIE_ESCOLA (id_escola, nome, cidade, uf, tipo, codigo_inep)
VALUES (SQ_CURIE_ESCOLA.NEXTVAL, 'E.E. Prof. Caetano de Campos', 'Sao Paulo', 'SP', 'PUBLICA', '35000001');
INSERT INTO T_CURIE_ESCOLA (id_escola, nome, cidade, uf, tipo, codigo_inep)
VALUES (SQ_CURIE_ESCOLA.NEXTVAL, 'ETEC Jorge Street', 'Sao Caetano do Sul', 'SP', 'PUBLICA', '35000002');
INSERT INTO T_CURIE_ESCOLA (id_escola, nome, cidade, uf, tipo, codigo_inep)
VALUES (SQ_CURIE_ESCOLA.NEXTVAL, 'Colegio Objetivo Paulista', 'Sao Paulo', 'SP', 'PRIVADA', '35000003');
INSERT INTO T_CURIE_ESCOLA (id_escola, nome, cidade, uf, tipo, codigo_inep)
VALUES (SQ_CURIE_ESCOLA.NEXTVAL, 'E.E. Dr. Alfredo Pujol', 'Guarulhos', 'SP', 'PUBLICA', '35000004');
INSERT INTO T_CURIE_ESCOLA (id_escola, nome, cidade, uf, tipo, codigo_inep)
VALUES (SQ_CURIE_ESCOLA.NEXTVAL, 'Colegio Estadual Pedro II', 'Rio de Janeiro', 'RJ', 'PUBLICA', NULL);

-- 3.2 Usuarios ---------------------------------------------------------
INSERT INTO T_CURIE_USUARIO (id_usuario, id_escola, nome, email, perfil, pontos)
VALUES (SQ_CURIE_USUARIO.NEXTVAL, 1, 'Ana Beatriz Souza', 'ana.souza@aluno.curie.edu.br', 'ALUNO', 320);
INSERT INTO T_CURIE_USUARIO (id_usuario, id_escola, nome, email, perfil, pontos)
VALUES (SQ_CURIE_USUARIO.NEXTVAL, 1, 'Carlos Eduardo Lima', 'carlos.lima@prof.curie.edu.br', 'PROFESSOR', 0);
INSERT INTO T_CURIE_USUARIO (id_usuario, id_escola, nome, email, perfil, pontos)
VALUES (SQ_CURIE_USUARIO.NEXTVAL, 2, 'Juliana Ferreira', 'juliana.ferreira@aluno.curie.edu.br', 'ALUNO', 210);
INSERT INTO T_CURIE_USUARIO (id_usuario, id_escola, nome, email, perfil, pontos)
VALUES (SQ_CURIE_USUARIO.NEXTVAL, 3, 'Rafael Nogueira', 'rafael.nogueira@aluno.curie.edu.br', 'ALUNO', 150);
INSERT INTO T_CURIE_USUARIO (id_usuario, id_escola, nome, email, perfil, pontos)
VALUES (SQ_CURIE_USUARIO.NEXTVAL, 4, 'Mariana Costa', 'mariana.costa@aluno.curie.edu.br', 'ALUNO', 90);
INSERT INTO T_CURIE_USUARIO (id_usuario, id_escola, nome, email, perfil, pontos)
VALUES (SQ_CURIE_USUARIO.NEXTVAL, 5, 'Pedro Henrique Alves', 'pedro.alves@aluno.curie.edu.br', 'ALUNO', 60);

-- 3.3 Kits -------------------------------------------------------------
INSERT INTO T_CURIE_KIT (id_kit, id_escola, cod_serial, status, dt_ativacao)
VALUES (SQ_CURIE_KIT.NEXTVAL, 1, 'CURIE-2026-0001', 'ATIVO', TO_DATE('01/10/2026','DD/MM/YYYY'));
INSERT INTO T_CURIE_KIT (id_kit, id_escola, cod_serial, status, dt_ativacao)
VALUES (SQ_CURIE_KIT.NEXTVAL, 2, 'CURIE-2026-0002', 'ATIVO', TO_DATE('03/10/2026','DD/MM/YYYY'));
INSERT INTO T_CURIE_KIT (id_kit, id_escola, cod_serial, status, dt_ativacao)
VALUES (SQ_CURIE_KIT.NEXTVAL, 3, 'CURIE-2026-0003', 'ATIVO', TO_DATE('05/10/2026','DD/MM/YYYY'));
INSERT INTO T_CURIE_KIT (id_kit, id_escola, cod_serial, status, dt_ativacao)
VALUES (SQ_CURIE_KIT.NEXTVAL, 4, 'CURIE-2026-0004', 'MANUTENCAO', TO_DATE('06/10/2026','DD/MM/YYYY'));
INSERT INTO T_CURIE_KIT (id_kit, id_escola, cod_serial, status, dt_ativacao)
VALUES (SQ_CURIE_KIT.NEXTVAL, 5, 'CURIE-2026-0005', 'INATIVO', NULL);

-- 3.4 Experimentos -----------------------------------------------------
INSERT INTO T_CURIE_EXPERIMENTO (id_experimento, nome, objeto, categoria, cpm_min, cpm_max, nivel, explicacao, publicado)
VALUES (SQ_CURIE_EXPERIMENTO.NEXTVAL, 'Radiacao de fundo', 'Ambiente da sala', 'AMBIENTE', 10, 40, 1,
 'Mede a radiacao natural do ambiente (raios cosmicos e solo). E o ponto de partida para comparar todas as outras missoes.', 'S');
INSERT INTO T_CURIE_EXPERIMENTO (id_experimento, nome, objeto, categoria, cpm_min, cpm_max, nivel, explicacao, publicado)
VALUES (SQ_CURIE_EXPERIMENTO.NEXTVAL, 'A banana radioativa', 'Banana', 'ALIMENTO', 12, 50, 1,
 'A banana contem potassio-40, isotopo radioativo natural. Mostra que a radiacao esta nos alimentos e no proprio corpo.', 'S');
INSERT INTO T_CURIE_EXPERIMENTO (id_experimento, nome, objeto, categoria, cpm_min, cpm_max, nivel, explicacao, publicado)
VALUES (SQ_CURIE_EXPERIMENTO.NEXTVAL, 'Sal light', 'Cloreto de potassio (KCl)', 'ALIMENTO', 20, 120, 2,
 'O sal light substitui sodio por potassio, e o potassio-40 eleva a contagem. Ensina que a dose e o que importa.', 'S');
INSERT INTO T_CURIE_EXPERIMENTO (id_experimento, nome, objeto, categoria, cpm_min, cpm_max, nivel, explicacao, publicado)
VALUES (SQ_CURIE_EXPERIMENTO.NEXTVAL, 'Granito da bancada', 'Amostra de granito', 'MINERAL', 15, 80, 2,
 'Rochas como o granito contem tracos de uranio e torio. Radiacao de fundo varia com a geologia do lugar.', 'S');
INSERT INTO T_CURIE_EXPERIMENTO (id_experimento, nome, objeto, categoria, cpm_min, cpm_max, nivel, explicacao, publicado)
VALUES (SQ_CURIE_EXPERIMENTO.NEXTVAL, 'Detector de fumaca', 'Detector ionico (Am-241)', 'DOMESTICO', 30, 400, 3,
 'Detectores ionicos usam uma fonte selada de americio-241 para ionizar o ar. Tecnologia nuclear que salva vidas em casa.', 'S');
INSERT INTO T_CURIE_EXPERIMENTO (id_experimento, nome, objeto, categoria, cpm_min, cpm_max, nivel, explicacao, publicado)
VALUES (SQ_CURIE_EXPERIMENTO.NEXTVAL, 'Medicina nuclear', 'Conteudo teorico', 'SAUDE', 0, 0, 3,
 'Missao teorica: como radiofarmacos produzidos no IPEN sao usados em diagnostico e tratamento. Sem medicao.', 'N');

-- 3.5 Medicoes ---------------------------------------------------------
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 1, 1, 1, 22.0, TO_TIMESTAMP('07/10/2026 09:15','DD/MM/YYYY HH24:MI'), 'Sala 12, janela fechada');
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 1, 1, 2, 31.5, TO_TIMESTAMP('07/10/2026 09:30','DD/MM/YYYY HH24:MI'), 'Banana madura');
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 1, 1, 3, 78.0, TO_TIMESTAMP('07/10/2026 09:45','DD/MM/YYYY HH24:MI'), NULL);
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 2, 3, 1, 19.0, TO_TIMESTAMP('08/10/2026 14:05','DD/MM/YYYY HH24:MI'), 'Laboratorio de quimica');
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 2, 3, 4, 46.0, TO_TIMESTAMP('08/10/2026 14:20','DD/MM/YYYY HH24:MI'), 'Granito cinza');
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 3, 4, 2, 28.0, TO_TIMESTAMP('09/10/2026 10:10','DD/MM/YYYY HH24:MI'), NULL);
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 3, 4, 5, 215.0, TO_TIMESTAMP('09/10/2026 10:30','DD/MM/YYYY HH24:MI'), 'Detector aberto, sensor a 2 cm');
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 4, 5, 1, 25.0, TO_TIMESTAMP('10/10/2026 08:50','DD/MM/YYYY HH24:MI'), NULL);
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 1, 2, 1, 21.0, TO_TIMESTAMP('10/10/2026 11:00','DD/MM/YYYY HH24:MI'), 'Calibracao pelo professor');
INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao)
VALUES (SQ_CURIE_MEDICAO.NEXTVAL, 2, 3, 3, 84.5, TO_TIMESTAMP('11/10/2026 15:40','DD/MM/YYYY HH24:MI'), 'Sal light marca B');

COMMIT;

-- =====================================================================
-- 4) CONSULTAS COM JOIN
-- =====================================================================

-- 4.1 Quais experimentos cada usuario ja concluiu, em qual escola e com
--     qual resultado? (base da tela "Minhas missoes" do app)
SELECT u.nome                                   AS usuario,
       u.perfil,
       e.nome                                   AS escola,
       x.nome                                   AS experimento,
       m.valor_cpm,
       TO_CHAR(m.dt_hora, 'DD/MM/YYYY HH24:MI') AS realizado_em,
       CASE
         WHEN m.valor_cpm BETWEEN x.cpm_min AND x.cpm_max THEN 'DENTRO DA FAIXA'
         ELSE 'FORA DA FAIXA'
       END                                      AS avaliacao
  FROM T_CURIE_MEDICAO     m
  JOIN T_CURIE_USUARIO     u ON u.id_usuario     = m.id_usuario
  JOIN T_CURIE_ESCOLA      e ON e.id_escola      = u.id_escola
  JOIN T_CURIE_EXPERIMENTO x ON x.id_experimento = m.id_experimento
 ORDER BY u.nome, m.dt_hora;

-- 4.2 Media de radiacao por experimento e por cidade, com quantidade de
--     medicoes e de escolas participantes (base do mapa colaborativo)
SELECT x.nome                      AS experimento,
       e.cidade,
       e.uf,
       COUNT(m.id_medicao)         AS qtd_medicoes,
       COUNT(DISTINCT e.id_escola) AS qtd_escolas,
       ROUND(AVG(m.valor_cpm), 1)  AS media_cpm,
       MIN(m.valor_cpm)            AS min_cpm,
       MAX(m.valor_cpm)            AS max_cpm
  FROM T_CURIE_MEDICAO     m
  JOIN T_CURIE_KIT         k ON k.id_kit         = m.id_kit
  JOIN T_CURIE_ESCOLA      e ON e.id_escola      = k.id_escola
  JOIN T_CURIE_EXPERIMENTO x ON x.id_experimento = m.id_experimento
 GROUP BY x.nome, e.cidade, e.uf
 ORDER BY x.nome, e.uf, e.cidade;

-- 4.3 (extra) Ranking de escolas: pontos dos alunos e medicoes enviadas
SELECT e.nome                                    AS escola,
       e.cidade,
       COUNT(DISTINCT u.id_usuario)              AS qtd_usuarios,
       NVL(SUM(u.pontos), 0)                     AS pontos_total,
       (SELECT COUNT(*)
          FROM T_CURIE_MEDICAO m
          JOIN T_CURIE_KIT k ON k.id_kit = m.id_kit
         WHERE k.id_escola = e.id_escola)        AS qtd_medicoes
  FROM T_CURIE_ESCOLA  e
  LEFT JOIN T_CURIE_USUARIO u ON u.id_escola = e.id_escola
 GROUP BY e.id_escola, e.nome, e.cidade
 ORDER BY pontos_total DESC, qtd_medicoes DESC;

-- Fim do script -------------------------------------------------------
