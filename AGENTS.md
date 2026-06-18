# AGENTS.md

Repositório monorepo destinado a abrigar múltiplas APIs no futuro.
Este documento define as regras e convenções que devem ser seguidas por qualquer
agente (humano ou automatizado) que atue no projeto.

## Estrutura do Projeto

- Monorepo com múltiplas APIs (a primeira API existente é `bankcore`).
- Novas APIs devem ser adicionadas como diretórios irmãos na raiz do repositório.

## Regras de Desenvolvimento

Ao implementar uma tarefa:

- Faça apenas a alteração solicitada.
- Não implemente funcionalidades futuras.
- Não faça refatorações não solicitadas.
- Não altere arquivos fora do escopo da tarefa.
- Prefira soluções simples.
- Explique sempre quais arquivos foram modificados.
- Para cada alteração e arquivo criado na API, gere testes unitários validando as mudanças.
- Commits só podem ser feitos se nenhum teste unitário estiver quebrado.
- Commits só podem ser feitos quando explicitamente solicitado pelo usuário.

## Estratégia de Commits

Objetivo:

- Commits pequenos.
- Fácil revisão humana.
- Fácil entendimento do histórico.

Evitar:

- Commits com múltiplas funcionalidades.
- Grandes refatorações.
- Mudanças arquiteturais não solicitadas.

Meta:

- Preferencialmente menos de 300 linhas alteradas por commit.

Padrão:

- Commits semânticos no formato `tipo: mensagem`.
- Tipos permitidos: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`.

Exemplos:

- `feat: add docker-compose with PostgreSQL and RabbitMQ`
- `fix: correct account balance calculation`
- `chore: update AGENTS.md with commit convention`
