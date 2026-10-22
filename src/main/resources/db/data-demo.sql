-- Carga de demonstracao para o perfil demo (H2). Mesmos dados do script Oracle.
INSERT INTO T_CURIE_ESCOLA (id_escola, nome, cidade, uf, tipo, codigo_inep, dt_cadastro) VALUES
 (1, 'E.E. Prof. Caetano de Campos', 'Sao Paulo', 'SP', 'PUBLICA', '35000001', CURRENT_DATE),
 (2, 'ETEC Jorge Street', 'Sao Caetano do Sul', 'SP', 'PUBLICA', '35000002', CURRENT_DATE),
 (3, 'Colegio Objetivo Paulista', 'Sao Paulo', 'SP', 'PRIVADA', '35000003', CURRENT_DATE),
 (4, 'E.E. Dr. Alfredo Pujol', 'Guarulhos', 'SP', 'PUBLICA', '35000004', CURRENT_DATE),
 (5, 'Colegio Estadual Pedro II', 'Rio de Janeiro', 'RJ', 'PUBLICA', NULL, CURRENT_DATE);

INSERT INTO T_CURIE_USUARIO (id_usuario, id_escola, nome, email, perfil, pontos, dt_cadastro) VALUES
 (1, 1, 'Ana Beatriz Souza', 'ana.souza@aluno.curie.edu.br', 'ALUNO', 320, CURRENT_DATE),
 (2, 1, 'Carlos Eduardo Lima', 'carlos.lima@prof.curie.edu.br', 'PROFESSOR', 0, CURRENT_DATE),
 (3, 2, 'Juliana Ferreira', 'juliana.ferreira@aluno.curie.edu.br', 'ALUNO', 210, CURRENT_DATE),
 (4, 3, 'Rafael Nogueira', 'rafael.nogueira@aluno.curie.edu.br', 'ALUNO', 150, CURRENT_DATE),
 (5, 4, 'Mariana Costa', 'mariana.costa@aluno.curie.edu.br', 'ALUNO', 90, CURRENT_DATE),
 (6, 5, 'Pedro Henrique Alves', 'pedro.alves@aluno.curie.edu.br', 'ALUNO', 60, CURRENT_DATE);

INSERT INTO T_CURIE_KIT (id_kit, id_escola, cod_serial, status, dt_ativacao) VALUES
 (1, 1, 'CURIE-2026-0001', 'ATIVO', DATE '2026-10-01'),
 (2, 2, 'CURIE-2026-0002', 'ATIVO', DATE '2026-10-03'),
 (3, 3, 'CURIE-2026-0003', 'ATIVO', DATE '2026-10-05'),
 (4, 4, 'CURIE-2026-0004', 'MANUTENCAO', DATE '2026-10-06'),
 (5, 5, 'CURIE-2026-0005', 'INATIVO', NULL);

INSERT INTO T_CURIE_MEDICAO (id_medicao, id_kit, id_usuario, id_experimento, valor_cpm, dt_hora, observacao) VALUES
 (1, 1, 1, 1, 22.0, TIMESTAMP '2026-10-07 09:15:00', 'Sala 12, janela fechada'),
 (2, 1, 1, 2, 31.5, TIMESTAMP '2026-10-07 09:30:00', 'Banana madura'),
 (3, 1, 1, 3, 78.0, TIMESTAMP '2026-10-07 09:45:00', NULL),
 (4, 2, 3, 1, 19.0, TIMESTAMP '2026-10-08 14:05:00', 'Laboratorio de quimica'),
 (5, 2, 3, 4, 46.0, TIMESTAMP '2026-10-08 14:20:00', 'Granito cinza'),
 (6, 3, 4, 2, 28.0, TIMESTAMP '2026-10-09 10:10:00', NULL),
 (7, 3, 4, 5, 215.0, TIMESTAMP '2026-10-09 10:30:00', 'Detector aberto, sensor a 2 cm'),
 (8, 4, 5, 1, 25.0, TIMESTAMP '2026-10-10 08:50:00', NULL),
 (9, 1, 2, 1, 21.0, TIMESTAMP '2026-10-10 11:00:00', 'Calibracao pelo professor'),
 (10, 2, 3, 3, 84.5, TIMESTAMP '2026-10-11 15:40:00', 'Sal light marca B');

ALTER SEQUENCE SQ_CURIE_ESCOLA RESTART WITH 100;
ALTER SEQUENCE SQ_CURIE_USUARIO RESTART WITH 100;
ALTER SEQUENCE SQ_CURIE_KIT RESTART WITH 100;
ALTER SEQUENCE SQ_CURIE_MEDICAO RESTART WITH 100;
