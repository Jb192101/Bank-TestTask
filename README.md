# Bank Card Management System

REST API для системы управления банковскими картами с аутентификацией JWT и ролевой моделью.

## 🚀 Технологии

- Java 17
- Spring Boot 3.2.4
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Liquibase
- OpenAPI 3.0 (Swagger)
- Docker

## 📦 Запуск приложения

### 1. Локальный запуск

```bash
# Клонирование репозитория
git clone <repository-url>
cd bank-card-system

# Запуск PostgreSQL через Docker
docker run -d --name bank-postgres \
  -e POSTGRES_DB=bank_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 postgres:15

# Сборка и запуск приложения
mvn spring-book:run
