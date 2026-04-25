# Sistema de Agenda Online - Documentação

## 🎯 Objetivo
Criar uma plataforma online para que usuários possam se registrar, fazer login e marcar agendamentos de atendimento com data e horário.

## ✨ Funcionalidades Implementadas

### 1. **Modelo de Dados**
- **Agendamento.java**: Modelo completo para armazenar informações de agendamentos
  - ID único (UUID)
  - Email do usuário
  - Nome do usuário
  - Data e hora do agendamento
  - Descrição/motivo do atendimento
  - Status (confirmado, pendente, cancelado)
  - Timestamp para rastreamento

### 2. **Serviço de Agendamento (AgendamentoService.java)**
- ✅ Criar novo agendamento com validação de horário disponível
- ✅ Obter agendamentos de um usuário específico
- ✅ Obter horários disponíveis para uma data (8:00 às 17:00)
- ✅ Cancelar agendamento
- ✅ Validação para evitar agendamentos em horários já ocupados

### 3. **Rotas Implementadas no Controller**

#### `/agenda` (GET)
- Exibe lista de agendamentos do usuário logado
- Mostra todos os agendamentos com data, hora e status
- Botão para criar novo agendamento
- Opção para cancelar agendamentos

#### `/agendar` (GET)
- Formulário para criar novo agendamento
- Seleção de data (mínimo 1 dia de antecedência)
- Seleção de horário (horários disponíveis carregam dinamicamente)
- Campo opcional para descrição do atendimento

#### `/agendar` (POST)
- Processa o envio do novo agendamento
- Valida data e horário
- Verifica se horário está disponível
- Redireciona para página de sucesso

#### `/cancelar-agendamento` (POST)
- Cancela um agendamento existente
- Verifica permissão do usuário
- Redireciona para lista de agendamentos

### 4. **Templates HTML Criados**

#### `agenda.html`
**Página de listagem de agendamentos do usuário**
- Design responsivo e moderno
- Exibe todos os agendamentos com cores e status
- Card para cada agendamento com:
  - Data e hora formatadas
  - Status visual (confirmado/cancelado/pendente)
  - Descrição do agendamento
  - Botão de cancelamento
- Botão para criar novo agendamento
- Mensagem quando não há agendamentos

#### `agendar.html`
**Formulário para novo agendamento**
- Interface intuitiva e responsiva
- Seletor de data com validação de data mínima
- Seletor de horários disponíveis (atualiza com AJAX)
- Campo de descrição/motivo
- Validações em tempo real
- Spinner de carregamento de horários
- Informações sobre horário de atendimento

#### `agendoSucesso.html`
**Página de confirmação após agendamento**
- Animações e design amigável
- Mensagem de sucesso personalizada
- Contagem regressiva automática (3 segundos)
- Redirecionamento automático para lista de agendamentos
- Botões de ação rápida

### 5. **Integração com Sistema Existente**

#### Atualização do Login
- Nova variável `usuariosLogados` para rastrear usuário logado
- Armazena email e nome do usuário após login bem-sucedido
- Template de login atualizado com botão "Minha Agenda"

#### Fluxo Completo
1. Usuário faz login em `/login`
2. Após sucesso, vê botões incluindo "Minha Agenda"
3. Clica em "Minha Agenda" para acessar `/agenda`
4. Na agenda, pode:
   - Ver todos seus agendamentos
   - Clicar em "+ Novo Agendamento"
   - Selecionar data e horário
   - Descrever o motivo (opcional)
   - Confirmar agendamento
   - Ver página de sucesso com redirecionamento automático

## 🎨 Design e UX

### Paleta de Cores
- Gradiente primário: #667eea → #764ba2 (roxo/azul)
- Verde de sucesso: #28a745
- Vermelho de ação/cancelamento: #dc3545
- Azul de informação: #007bff

### Responsividade
- Totalmente responsivo para mobile, tablet e desktop
- Adaptações de layout em telas pequenas
- Botões e campos otimizados para toque

### Animações
- Transições suaves em todos os elementos
- Animação de bounce no ícone de sucesso
- Preenchimento de barra de carregamento
- Slide-in para containers

## 📊 Horários de Funcionamento

- **Horários disponíveis**: 8:00 às 17:00 (9 slots de 1 hora)
- **Antecedência mínima**: 1 dia
- **Intervalo entre agendamentos**: 1 hora

## 💾 Armazenamento de Dados

Atualmente, todos os dados são armazenados em memória (ArrayList/HashMap):
- `agendamentos`: Lista de todos os agendamentos do sistema
- `usuariosLogados`: Mapa com usuário atualmente logado

**Para produção**, considere:
- Banco de dados relacional (MySQL, PostgreSQL)
- Persistência em arquivo
- Cache distribuído

## 🚀 Como Usar

### 1. Iniciar a Aplicação
```bash
cd C:\Users\leviv\OneDrive\Desktop\agenda\agenda
.\mvnw.cmd spring-boot:run
```

### 2. Acessar a Aplicação
- URL: `http://localhost:8080`
- Automaticamente redireciona para `/login`

### 3. Fluxo de Teste
1. **Cadastro**: Clique em "Cadastre-se aqui"
2. **Preenchimento**: Nome, Email, Senha, Confirmação de Senha
3. **Celular**: Digite celular (será pedido após cadastro)
4. **Código de Verificação**: Será enviado por SMS/WhatsApp
5. **Login**: Faça login com email e senha cadastrados
6. **Agenda**: Clique em "Minha Agenda"
7. **Novo Agendamento**: Clique em "+ Novo Agendamento"
8. **Seleção**: Escolha data e horário
9. **Confirmação**: Veja página de sucesso e redirecionamento

## 📁 Estrutura de Arquivos

```
src/main/
├── java/br/com/levima/agenda/
│   ├── controller/
│   │   └── HomeController.java (atualizado com rotas de agenda)
│   ├── model/
│   │   ├── Agendamento.java (NOVO)
│   │   ├── Contato.java (existente)
│   │   └── CadastroTemporario.java (existente)
│   └── service/
│       ├── AgendamentoService.java (NOVO)
│       └── VerificacaoService.java (existente)
└── resources/
    └── templates/
        ├── agenda.html (NOVO)
        ├── agendar.html (NOVO)
        ├── agendoSucesso.html (NOVO)
        ├── login.html (atualizado)
        └── outros templates existentes
```

## 🔧 Tecnologias Utilizadas

- **Framework**: Spring Boot 4.0.6
- **Template Engine**: Thymeleaf
- **Java**: OpenJDK 17
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Build**: Maven

## 📋 Funcionalidades Futuras Sugeridas

1. **Notificações**
   - Email de confirmação de agendamento
   - Lembrete 24h antes do agendamento
   - SMS/WhatsApp de confirmação

2. **Edição de Agendamento**
   - Permitir mudança de data/horário
   - Histórico de agendamentos

3. **Admin**
   - Painel para visualizar todos agendamentos
   - Relatórios de ocupação
   - Configuração de horários

4. **Persistência**
   - Integração com banco de dados
   - Backup automático
   - Auditoria de operações

5. **Autenticação**
   - Integração com OAuth/Social Login
   - Recuperação de senha
   - Autenticação de dois fatores

## ✅ Testes Manuais Recomendados

- [ ] Login e acesso à agenda
- [ ] Criar novo agendamento
- [ ] Validação de datas passadas
- [ ] Verificação de horários ocupados
- [ ] Cancelamento de agendamento
- [ ] Responsividade em mobile
- [ ] Redirecionamento automático após sucesso
- [ ] Validação de campos vazios

## 🐛 Notas Importantes

- O sistema valida automaticamente:
  - Data deve ser futura (mínimo amanhã)
  - Horário deve estar disponível
  - Usuário deve estar logado
  - Email é único para cada agendamento

- O redirecionamento após novo agendamento ocorre automaticamente em 3 segundos

- Os horários disponíveis são gerados dinamicamente baseado em agendamentos existentes

---

**Versão**: 1.0  
**Data de Criação**: 25/04/2026  
**Status**: ✅ Funcional

