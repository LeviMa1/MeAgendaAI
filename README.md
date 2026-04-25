# MeAgendaAI - Aplicação de Agenda

## ✅ Status Atual

### Funcionalidades Implementadas:
- ✅ Cadastro de usuário (Nome, Email, Senha)
- ✅ Confirmação de celular
- ✅ Envio de código de verificação por EMAIL (GRATUITO)
- ✅ Validação de código
- ✅ Login com email e senha
- ✅ Gerenciamento de contatos
- ✅ Lista de contatos cadastrados
- ✅ **[NOVO] Sistema de Agenda Online**
- ✅ **[NOVO] Marcação de agendamentos com data e hora**
- ✅ **[NOVO] Visualização de agendamentos do usuário**
- ✅ **[NOVO] Cancelamento de agendamentos**
- ✅ **[NOVO] Validação automática de horários disponíveis**

### Tecnologias:
- Spring Boot 4.0.6
- Thymeleaf para templates
- Spring Mail para envio de emails
- Java 17
- HTML5 + CSS3 + JavaScript Vanilla
- LocalDate/LocalTime para manipulação de datas

## 🚀 Como Executar

### 1. Modo Teste (Sem Email - Recomendado para Desenvolvimento)

```bash
cd "C:\Users\leviv\OneDrive\Desktop\agenda\agenda"
.\mvnw.cmd spring-boot:run
```

Acesse: `http://localhost:8080`

**Neste modo:**
- Códigos aparecem no console
- Sem necessidade de configuração
- Perfeito para testes locais

### 2. Modo Produção (Com Email Real)

#### Passo 1: Configurar Gmail
1. Crie uma "Senha de App" em: https://myaccount.google.com/apppasswords
2. Configure as variáveis de ambiente:

```bash
$env:MAIL_USERNAME = "seu-email@gmail.com"
$env:MAIL_PASSWORD = "sua-senha-de-app-16-caracteres"
```

#### Passo 2: Executar
```bash
.\mvnw.cmd spring-boot:run
```

**Neste modo:**
- Emails reais são enviados
- Usuários recebem código por email
- Totalmente funcional

## 📚 Documentação

- **EMAIL_SETUP.md** - Como configurar email com Gmail
- **WHATSAPP_SETUP.md** - Documentação anterior (Twilio)
- **AGENDA_DOCUMENTATION.md** - [NOVO] Documentação técnica da agenda
- **GUIA_RAPIDO.md** - [NOVO] Guia de uso do sistema de agenda
- **IMPLEMENTATION_SUMMARY.md** - [NOVO] Resumo executivo completo

## 🔐 Fluxo de Cadastro

1. Usuário acessa `/cadastro`
2. Preencha: Nome, Email, Senha, Confirmação
3. Clica em "Cadastrar"
4. Sistema redireciona para `/pedir-celular`
5. Usuário informa celular
6. Sistema envia código por email
7. Usuário digita código em `/verificar-codigo`
8. Se correto, cadastro é concluído
9. Redireciona para `/login`

## 🔑 Fluxo de Login

1. Usuário acessa `/login`
2. Informa Email e Senha
3. Se correto, exibe mensagem de boas-vindas
4. Pode acessar:
   - **"Minha Agenda"** - Para gerenciar agendamentos (NOVO)
   - **"Ver Contatos"** - Para ver lista de contatos

## 📅 [NOVO] Fluxo de Agendamento

1. Após login, clique em **"Minha Agenda"**
2. Veja lista de seus agendamentos
3. Clique em **"+ Novo Agendamento"**
4. Selecione uma **data futura** (mínimo amanhã)
5. Escolha um **horário disponível** (8:00 às 17:00)
6. Opcionalmente, descreva o motivo do agendamento
7. Clique em **"Confirmar Agendamento"**
8. Veja página de sucesso com redirecionamento automático
9. Agendamento aparece na sua lista com status "Confirmado"
10. Pode cancelar clicando em **"Cancelar Agendamento"**

## 📁 Estrutura do Projeto

```
src/main/java/br/com/levima/agenda/
├── controller/
│   └── HomeController.java          # Controlador principal (atualizado)
├── model/
│   ├── Contato.java                 # Modelo de contato
│   ├── CadastroTemporario.java       # Dados temporários
│   └── Agendamento.java             # [NOVO] Modelo de agendamento
└── service/
    ├── VerificacaoService.java       # Serviço de email
    ├── AgendamentoService.java       # [NOVO] Serviço de agendamentos
    └── WhatsAppService.java          # Obsoleto (pode deletar)

src/main/resources/
├── templates/
│   ├── cadastro.html                # Formulário de cadastro
│   ├── pedirCelular.html            # Pedir celular
│   ├── verificarCodigo.html         # Verificar código
│   ├── cadastroSucesso.html         # Sucesso no cadastro
│   ├── login.html                   # Formulário de login (atualizado)
│   ├── listaContatos.html           # Lista de contatos
│   ├── agenda.html                  # [NOVO] Listagem de agendamentos
│   ├── agendar.html                 # [NOVO] Formulário de agendamento
│   └── agendoSucesso.html           # [NOVO] Confirmação de agendamento
└── application.properties            # Configurações
```

### Rotas Novas (Sistema de Agenda)
```
GET  /agenda                    # Listar agendamentos do usuário
GET  /agendar                   # Formulário de novo agendamento
POST /agendar                   # Criar novo agendamento
GET  /horarios-disponiveis      # AJAX para horários livres
POST /cancelar-agendamento      # Cancelar agendamento
```

## 🐛 Troubleshooting

### Erro: "Cannot resolve symbol 'mail'"
- Este é um erro do IDE (cache)
- O Maven compilou com sucesso
- Recarregue o projeto no IDE

### Aplicação não inicia
- Verifique se porta 8080 está livre
- Tente: `.\mvnw.cmd clean compile`

### Email não chega
- Verifique MAIL_USERNAME e MAIL_PASSWORD
- Verifique pasta de spam
- Em modo teste, veja o console

## 📝 Próximos Passos

1. ✅ Testar cadastro e login
2. ✅ Testar envio de email
3. ✅ Testar validação de código
4. ✅ **[NOVO] Testar sistema de agenda**
5. ✅ **[NOVO] Testar marcação de agendamentos**
6. [ ] Adicionar edição de agendamentos
7. [ ] Adicionar notificações de lembrete
8. [ ] Adicionar painel administrativo
9. [ ] Adicionar banco de dados (JPA)
10. [ ] Fazer deploy

## 💡 Notas Importantes

- **Credenciais não são persistidas** - Dados são armazenados em memória
- **Sem banco de dados** - Use JPA/Hibernate para produção
- **Email é gratuito** - Usando Gmail SMTP
- **Código expira em 10 minutos** - Segurança
- **[NOVO] Agendamentos em memória** - Migrar para DB em produção
- **[NOVO] Horários: 8:00 às 17:00** - 9 slots de 1 hora cada
- **[NOVO] Antecedência mínima: 1 dia** - Sem agendamentos para hoje

## 📞 Suporte

Para dúvidas sobre Spring Boot: https://spring.io/guides
Para dúvidas sobre Gmail SMTP: https://support.google.com/mail

