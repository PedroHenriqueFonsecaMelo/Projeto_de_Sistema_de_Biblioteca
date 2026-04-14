# Projeto de Sistema de Biblioteca

Sistema de gestão de biblioteca desenvolvido em Java com Spring Boot, MongoDB
embutido, Thymeleaf e segurança via Spring Security.

## Descrição

Este projeto oferece um painel administrativo para gerenciamento de acervo,
empréstimos, reservas, controle de usuários e relatórios de movimentação. A
aplicação usa Spring Boot e Thymeleaf para exibir páginas web e inclui dados
iniciais para demonstração.

## Funcionalidades principais

- Autenticação de usuário administrativo
- Cadastro e remoção de livros no acervo
- Visualização e pesquisa do acervo
- Gestão de empréstimos com controle de atrasos e multas
- Cadastro de reservas e liberação de reservas
- Relatórios de movimentação por período
- Controle de usuários e permissões

## Pré-requisitos

- Java 17 ou superior instalado
- Maven instalado (opcional, pois o projeto inclui `mvnw`/`mvnw.cmd`)
- Acesso à internet apenas para baixar dependências na primeira execução

## Executando o projeto

1. Clone o repositório:

```bash
git clone https://github.com/PedroHenriqueFonsecaMelo/Projeto_de_Sistema_de_Biblioteca.git
cd Projeto_de_Sistema_de_Biblioteca
```

2. Execute a aplicação:

No Windows:

```powershell
./mvnw.cmd clean install spring-boot:run
```

Ou executando pela propria IDE

3. Abra o navegador e acesse:

```text
http://localhost:8080/
```

4. Faça login com o usuário administrativo padrão:

- E-mail: `admin@library.com`
- Senha: `admin123`

## Endpoints importantes

- Página inicial / login: `http://localhost:8080/`
- Dashboard: `http://localhost:8080/library/dashboard`
- Acervo: `http://localhost:8080/library/livros/acervo`
- Empréstimos: `http://localhost:8080/library/emprestimos`
- Reservas: `http://localhost:8080/library/reservas`
- Relatórios: `http://localhost:8080/library/relatorios`
- Controle de usuários: `http://localhost:8080/library/controle`

## Estrutura do projeto

- `src/main/java/br/umc/demo/` — código-fonte Java
- `src/main/resources/templates/` — páginas Thymeleaf
- `src/main/resources/static/` — estilos e scripts estáticos
- `pom.xml` — dependências e configuração do Maven

## Observações

- O projeto carrega um conjunto de dados iniciais pelo `DataInitializer` na
  primeira execução.
- O banco de dados usado para desenvolvimento é o MongoDB embutido via
  `de.flapdoodle.embed.mongo`.
- Caso precise limpar os dados de demonstração, pare a aplicação e remova o
  diretório `target` antes de iniciar novamente.
- As vezes é aconselhavel fazer uma limpeza e reinstallação das bibliotecas do
  MAVEN

## Link do repositório

https://github.com/PedroHenriqueFonsecaMelo/Projeto_de_Sistema_de_Biblioteca
