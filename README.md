# CURIE API — Java Advanced · Sprint 1

API RESTful do **Projeto CURIE** — *“Um laboratório nuclear em cada escola por R$ 300”* — desenvolvida para o Challenge FIAP 2026 em parceria com o **CTMSP / Marinha do Brasil**.

## Grupo CURIE — Turma 2TDSPB

| Integrante | RM |
|---|---|
| Matheus Carneiro Maciel | 567753 |
| Murilo Marques | 568224 |
| Paulo Henrique da Silva Kian | 563343 |

## A solução

A tecnologia nuclear é pouco compreendida e associada quase só a usinas e acidentes. O CURIE leva um **kit de ciência cidadã** (ESP32 + sensor Geiger-Müller + leitor NFC, ~R$ 300) para dentro da escola: os alunos medem a radiação de objetos do cotidiano — banana, sal light, granito, detector de fumaça — guiados por missões em um app, e cada medição alimenta um **mapa colaborativo nacional de radiação de fundo**. Uma tutora de IA explica cada resultado citando fontes da CNEN e do CTMSP.

Esta API é o backend que gerencia **escolas, usuários (alunos e professores), kits e medições**. O catálogo de experimentos é gerenciado pela aplicação em .NET (funcionalidade complementar, não idêntica).

## Tecnologias

- Java 17 · Spring Boot 3.3.5
- Spring Web · Spring Data JPA · Spring Validation
- Oracle Database (FIAP) via `ojdbc11`
- springdoc-openapi (Swagger UI)
- Maven

## Arquitetura

```
br.com.fiap.curie
├── controller   → endpoints REST, documentados com OpenAPI
├── service      → regras de negócio e transações
├── repository   → Spring Data JPA (Query Methods)
├── model        → entidades JPA mapeadas nas tabelas T_CURIE_*
├── dto          → records de entrada (com Bean Validation) e saída
├── exception    → @RestControllerAdvice e formato padrão ApiError
└── config       → configuração do OpenAPI
```

### Modelo de dados

| Entidade | Tabela | Sequence |
|---|---|---|
| `Escola` | `T_CURIE_ESCOLA` | `SQ_CURIE_ESCOLA` |
| `Usuario` | `T_CURIE_USUARIO` | `SQ_CURIE_USUARIO` |
| `Kit` | `T_CURIE_KIT` | `SQ_CURIE_KIT` |
| `Medicao` | `T_CURIE_MEDICAO` | `SQ_CURIE_MEDICAO` |

`Medicao` referencia `T_CURIE_EXPERIMENTO` (catálogo gerenciado pelo .NET) pela coluna `id_experimento`.

## Requisitos da disciplina atendidos

| Requisito | Onde está |
|---|---|
| CRUD completo (Spring Data JPA) | `Escola`, `Usuario` e `Kit` com GET/POST/PUT/DELETE; `Medicao` com registro, consulta e exclusão |
| Busca por texto com Query Method | `findByNomeContainingIgnoreCase` em `EscolaRepository` e `UsuarioRepository`; `findByKitEscolaCidadeIgnoreCase` em `MedicaoRepository` |
| Spring Validation | `@NotBlank`, `@Email`, `@Pattern`, `@DecimalMin`… nos records em `dto/` |
| Tratamento global de exceções | `exception/GlobalExceptionHandler` (400, 404, 409, 422, 500) com corpo `ApiError` |
| Swagger / OpenAPI | `http://localhost:8080/swagger-ui.html` com endpoints, parâmetros e respostas documentados |
| Regras de negócio | kit precisa estar ATIVO para medir; aluno só mede com kit da própria escola; cada medição credita 10 pontos; ativar kit grava a data |

## Como executar

### 1. Banco de dados

Execute o script `src/main/resources/db/CURIE_Challenge_1Sem_2TDS_BD.sql` no Oracle da FIAP (SQL Developer → F5). Ele cria as tabelas, sequences e **os dados de teste** (5 escolas, 6 usuários, 5 kits, 6 experimentos, 10 medições) e pode ser reexecutado.

### 2. Credenciais (nunca versionadas)

Windows (PowerShell):
```powershell
$env:ORACLE_USER="RM567753"
$env:ORACLE_PASSWORD="sua_senha"
```

Linux / macOS:
```bash
export ORACLE_USER=RM567753
export ORACLE_PASSWORD=sua_senha
```

**Alternativa sem variáveis de ambiente:** crie o arquivo `src/main/resources/application-local.properties` (está no `.gitignore`, nunca é versionado) com:

```properties
spring.datasource.username=RM567753
spring.datasource.password=sua_senha
```

O perfil `local` é ativado por padrão e carrega esse arquivo quando ele existe. Um modelo está em `src/main/resources/application-local.properties.example`.

Opcional: `ORACLE_URL` para apontar outro banco (padrão `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`).

### 3. Rodar

```bash
mvn spring-boot:run
```

Ou pelo IntelliJ / VS Code, executando `CurieApiApplication` (com as variáveis de ambiente na *Run Configuration* ou com o `application-local.properties` criado).

### 4. Testar

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- Requisições prontas: `docs/requests.http` (extensão REST Client do VS Code ou HTTP Client do IntelliJ)

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/escolas?nome=&page=&size=` | Lista paginada / busca por nome |
| GET | `/api/escolas/cidade/{cidade}` | Escolas de uma cidade |
| GET / POST | `/api/escolas`, `/api/escolas/{id}` | Buscar, cadastrar |
| PUT / DELETE | `/api/escolas/{id}` | Atualizar, excluir |
| GET | `/api/usuarios?nome=` | Lista paginada / busca por nome |
| GET | `/api/usuarios/ranking` | Top 10 por pontos |
| GET | `/api/usuarios/escola/{idEscola}` | Usuários de uma escola |
| POST / PUT / DELETE | `/api/usuarios`, `/api/usuarios/{id}` | CRUD |
| GET | `/api/kits?status=` | Lista / filtro por status |
| GET | `/api/kits/serial/{serial}` | Identificação do kit pelo firmware |
| PATCH | `/api/kits/{id}/ativar` | Ativa o kit (regra de negócio) |
| POST / PUT / DELETE | `/api/kits`, `/api/kits/{id}` | CRUD |
| GET | `/api/medicoes` | Lista paginada, mais recentes primeiro |
| GET | `/api/medicoes/usuario/{id}` | “Minhas missões” |
| GET | `/api/medicoes/cidade/{cidade}` | Base do mapa colaborativo |
| POST | `/api/medicoes` | Registra leitura e credita pontos |

### Exemplo — registrar medição

```http
POST /api/medicoes
Content-Type: application/json

{
  "idKit": 1,
  "idUsuario": 1,
  "idExperimento": 2,
  "valorCpm": 33.5,
  "observacao": "Banana madura, sensor a 1 cm"
}
```

### Exemplo — erro de validação (400)

```json
{
  "timestamp": "2026-10-20T10:15:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Erro de validacao: verifique os campos informados",
  "path": "/api/escolas",
  "campos": [
    { "campo": "nome", "mensagem": "O nome e obrigatorio" },
    { "campo": "uf", "mensagem": "A UF deve ter exatamente 2 letras" }
  ]
}
```

## Próximas sprints

- Integração com as procedures e functions PL/SQL (Sprint 2 de Banco de Dados).
- Recebimento de medições do kit via MQTT.
- Autenticação e perfis de acesso.