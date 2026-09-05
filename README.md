# MeAgendaAI

Sistema de agenda online com cadastro, verificacao por SMS/WhatsApp, agendamentos e painel administrativo.

## Tecnologias

- Java 17 + Spring Boot 4.0.6
- Spring Security (CSRF, roles USER/ADMIN)
- Spring Data JPA + H2 (dev) / MySQL (prod)
- Thymeleaf + CSS compartilhado
- Actuator (health check)

## Executar localmente

```bash
cd agenda
.\mvnw.cmd spring-boot:run
```

Acesse: http://localhost:8080

### Credenciais padrao (dev)

| Tipo  | Usuario | Senha     |
|-------|---------|-----------|
| Admin | admin   | admin123  |

Configure `ADMIN_PASSWORD` em producao.

### Modo teste (OTP)

Sem Vonage/CallMeBot configurado, o codigo de verificacao aparece no **console** da aplicacao.

## Fluxos principais

### Usuario
1. `/cadastro` - criar conta
2. Informar celular e verificar codigo (SMS/WhatsApp/console)
3. `/login` - entrar
4. `/agenda` - ver agendamentos
5. `/agendar` - novo agendamento (status **pendente** ate admin confirmar)
6. `/agendar/editar?id=...` - editar/remarcar
7. `/perfil` - editar dados e senha
8. `/recuperar-senha` - recuperar senha via codigo

### Admin
1. Login com `admin` / senha configurada
2. `/admin` - dashboard, configurar disponibilidade, confirmar/rejeitar agendamentos
3. Exportar Excel (.xlsx) de agendamentos e contatos
4. Remover datas configuradas

## Variaveis de ambiente

| Variavel | Descricao |
|----------|-----------|
| `SPRING_PROFILES_ACTIVE` | `dev` (padrao) ou `prod` |
| `ADMIN_USERNAME` | Usuario admin (padrao: admin) |
| `ADMIN_PASSWORD` | Senha admin |
| `DATABASE_URL` | JDBC URL (MySQL em prod) |
| `DATABASE_USERNAME` | Usuario do banco |
| `DATABASE_PASSWORD` | Senha do banco |
| `VONAGE_API_KEY` / `VONAGE_API_SECRET` | SMS Vonage (opcional) |
| `CALLMEBOT_ENABLED` / `CALLMEBOT_APIKEY` | WhatsApp CallMeBot (opcional) |

## Docker Compose (MySQL + App)

```bash
docker-compose up --build
```

## Health check

```
GET http://localhost:8080/actuator/health
```

## Testes

```bash
.\mvnw.cmd test
```

## Estrutura

```
controller/   AuthController, AgendaController, AdminController, ApiController, PerfilController
service/      AgendamentoService, AdminService, ContatoService, CadastroPendenteService, LembreteService
security/     Spring Security customizado
model/        Contato, Agendamento, CadastroPendente, DisponibilidadeAdmin
```

## Melhorias implementadas

- Spring Security com CSRF e roles
- Senhas BCrypt
- Admin via variavel de ambiente
- Cadastro pendente persistido no banco (nao perde ao reiniciar)
- OTP com SecureRandom, rate limit e expiracao
- Validacao server-side de datas/horarios
- Lock pessimista contra reserva duplicada
- Confirmacao de agendamento pelo admin
- Lembretes automaticos (24h e 1h antes)
- Paginacao, export Excel, perfil, recuperar senha
- Profiles dev/prod, Docker Compose, CI GitHub Actions
