## Political Blog API - Backend Java

API REST completa para um blog político moderno desenvolvido com Spring Boot 3, PostgreSQL e JWT Authentication.

### Tecnologias Utilizadas

* Java
* Spring Boot
* Spring Security com JWT
* Spring Data JPA
* PostgreSQL
* Maven
* BCrypt
* Hibernate como ORM

### Funcionalidades

* Sistema de autenticação JWT com refresh tokens
* Gerenciamento completo de posts (CRUD)
* Sistema de categorias e tags
* Controle de acesso baseado em roles(ADMIN, AUTHOR, EDITOR, READER)
* Validação de dados
* Tratamento global de exceções
* CORS configurado
* Documentação Swagger/OpenAPI

### Pré-requisitos

* Java
* Maven
* PostgreSQL
* Git

### Estrutura do Projeto

```txt
src/main/java/com/politicabr/blog
├── config/                         # Configurações (Security, CORS, etc)
├── controller/                     # Controllers REST
├── dto/                            # Data Transfer Objects
├── entity/                         # Entidades JPA
├── exception/                      # Exceções customizadas
├── mapper/                         # Mapeamento Entity <-> DTO
├── repository/                     # Repositórios JPA
├── security/                       # JWT e configurações de segurança
├── service/                        # Lógica de negócio
└── util/                           # Utilitários
```

### Segurança

* Senhas são hash com BCrypt (custo 12)
* JWT tokens expiram em 24 horas
* Refresh tokens expiram em 7 dias
* CORS configurado para origens específicas
* Validação de entrada em todos os endpoints

## Contato

* **Autor**: Flavio Macedo
* **Email**: fhenrique609@gmail.com
* **GitHub**: @Flaviohmm