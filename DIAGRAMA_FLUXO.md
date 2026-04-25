# 🎯 FLUXO DO SISTEMA - Diagrama Visual

## 📊 Fluxo Completo da Aplicação

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     SISTEMA DE AGENDA ONLINE - MeAgendaAI                   │
└─────────────────────────────────────────────────────────────────────────────┘

                              ┌──────────────┐
                              │   INÍCIO     │
                              │ (localhost)  │
                              └──────┬───────┘
                                     │
                    ┌────────────────▼───────────────────┐
                    │     Usuário Logado?                │
                    └────────┬──────────────────┬────────┘
                             │                  │
                        NÃO  │                  │  SIM
                             │                  │
              ┌──────────────▼─┐        ┌───────▼──────────────┐
              │ Tela de Login  │        │ DASHBOARD PRINCIPAL  │
              │ /login         │        │ ✅ Minha Agenda      │
              └────────┬───────┘        │ ✅ Ver Contatos      │
                       │                │ ✅ Sair              │
           ┌───────────▼─────────────┐  └───────┬──────────────┘
           │ Opções de Acesso        │          │
           │ 1. Fazer Login          │          │
           │ 2. Cadastrar Nova Conta │          │
           │ 3. Recuperar Senha      │          │
           └───────────┬─────────────┘          │
                       │                        │
          ┌────────────▼──────────────┐         │
          │   FLUXO DE CADASTRO       │         │
          │                           │         │
          │ 1. Dados Pessoais         │         │
          │ 2. Email + Senha          │         │
          │ 3. Confirmação de Senha   │         │
          │ 4. Celular                │         │
          │ 5. Código de Verificação  │         │
          │ 6. Sucesso ✓              │         │
          │    (Redireciona p/ login) │         │
          └────────────┬──────────────┘         │
                       │                        │
         ┌─────────────▼──────────────┐         │
         │   FLUXO DE LOGIN           │         │
         │                            │         │
         │ 1. Informa Email           │         │
         │ 2. Informa Senha           │         │
         │ 3. Validação ✓             │         │
         │ 4. Redirecionado           │         │
         │    (Dashboard)             │         │
         └─────────────┬──────────────┘         │
                       │                        │
                       └────────────┬───────────┘
                                    │
                    ┌───────────────▼─────────────────┐
                    │   PÁGINA: MINHA AGENDA          │
                    │   /agenda                       │
                    │                                 │
                    │  ┌─────────────────────────────┐│
                    │  │ Bem-vindo, [Nome do Usuário]││
                    │  │                             ││
                    │  │ [+ Novo Agendamento]        ││
                    │  ├─────────────────────────────┤│
                    │  │ Meus Agendamentos:          ││
                    │  │                             ││
                    │  │ ┌─────────────────────────┐ ││
                    │  │ │ 28/04 às 10:00          │ ││
                    │  │ │ Status: ✅ Confirmado   │ ││
                    │  │ │ Motivo: Consulta Geral  │ ││
                    │  │ │ [Cancelar]              │ ││
                    │  │ └─────────────────────────┘ ││
                    │  │                             ││
                    │  │ ┌─────────────────────────┐ ││
                    │  │ │ 30/04 às 14:00          │ ││
                    │  │ │ Status: ✅ Confirmado   │ ││
                    │  │ │ Motivo: Acompanhamento  │ ││
                    │  │ │ [Cancelar]              │ ││
                    │  │ └─────────────────────────┘ ││
                    │  │                             ││
                    │  └─────────────────────────────┘│
                    └────────────┬────────────────────┘
                                 │
                    ┌────────────▼─────────────┐
                    │ Clica em:                 │
                    │                           │
                    │ 1. [Cancelar] ──────┐    │
                    │ 2. [+ Novo Agenda.] │    │
                    └────────┬──────────┬─┘    │
                             │          │      │
                    ┌────────▼─┐  ┌─────▼────┐ │
                    │ Cancela  │  │Novo Agend│ │
                    └────────┬─┘  └─────┬────┘ │
                             │          │      │
                    ┌────────▼──────────▼────────────┐
                    │   FORMULÁRIO: AGENDAR          │
                    │   /agendar                     │
                    │                                │
                    │ ┌────────────────────────────┐ │
                    │ │ Selecione a Data:          │ │
                    │ │ [Seletor de Data]  ──────┐│ │
                    │ │                          ││ │
                    │ │ Selecione o Horário:      │ │
                    │ │ ┌──────────────────────────┐│ │
                    │ │ │ 08:00  09:00  10:00 ✓   ││ │
                    │ │ │ 11:00  12:00  13:00     ││ │
                    │ │ │ 14:00  15:00  16:00     ││ │
                    │ │ └──────────────────────────┘│ │
                    │ │                            │ │
                    │ │ Descrição (opcional):      │ │
                    │ │ [TextArea]                 │ │
                    │ │                            │ │
                    │ │ [Confirmar] [Cancelar]    │ │
                    │ └────────────────────────────┘ │
                    └────────┬──────────────────────┘
                             │
                    ┌────────▼───────────┐
                    │ Validações:        │
                    │ • Data > Hoje      │
                    │ • Horário livre    │
                    │ • Campo preenchido │
                    │ • Usuário logado   │
                    └────────┬───────────┘
                             │
                    ┌────────▼────────────────────┐
                    │ PÁGINA: SUCESSO             │
                    │ /agendoSucesso              │
                    │                             │
                    │ ✅ Agendamento Confirmado!  │
                    │                             │
                    │ Parabéns, [Nome]!           │
                    │ Seu agendamento foi         │
                    │ realizado com sucesso.      │
                    │                             │
                    │ ▓▓▓▓▓▓▓░░░░░░░ (barra)      │
                    │ Redirecionando em: 3s       │
                    │                             │
                    │ [Ver Agendamentos]          │
                    │ [Voltar ao Início]          │
                    └────────┬────────────────────┘
                             │ (Redireciona após 3s)
                             │
                    ┌────────▼─────────────┐
                    │ VOLTA PARA AGENDA    │
                    │ (/agenda)            │
                    │ Mostra novo          │
                    │ agendamento na lista │
                    └─────────────────────┘
```

---

## 🔄 Fluxo de Estados do Agendamento

```
                    ┌──────────────┐
                    │   CRIADO     │
                    │ (Estado Novo)│
                    └──────┬───────┘
                           │
                           │ Validações OK
                           ▼
                    ┌──────────────────┐
                    │  CONFIRMADO ✅   │
                    │ (Pronto p/ Uso)  │
                    └────┬───────┬─────┘
                         │       │
                    Clica │       │ 3 dias antes
                    Canc. │       │ Lembrete?
                         │       │
                    ┌────▼───────▼──────┐
                    │   CANCELADO ❌    │
                    │ (Indisponível)    │
                    └───────────────────┘
```

---

## 🗓️ Fluxo de Horários

```
DATA: 28/04/2026 (Exemplo)

08:00 ────── [Disponível]
09:00 ────── [Disponível]
10:00 ────── [OCUPADO] ✗ (Outro usuário já agendou)
11:00 ────── [Disponível]
12:00 ────── [Disponível]
13:00 ────── [Disponível]
14:00 ────── [Disponível]
15:00 ────── [Disponível]
16:00 ────── [Disponível]

Usuário só consegue selecionar os [Disponível]
Os [OCUPADO] aparecem desabilitados na interface
```

---

## 📱 Fluxo Responsivo

```
DESKTOP (1024px+)                TABLET (768px-1024px)       MOBILE (< 768px)

┌─────────────────┐              ┌──────────────┐            ┌────────────┐
│    HEADER       │              │   HEADER     │            │  HEADER    │
├─────────────────┤              ├──────────────┤            ├────────────┤
│ Logo   Menu     │              │  Logo Menu   │            │ Logo ☰Menu │
├─────────────────┤              ├──────────────┤            ├────────────┤
│                 │              │              │            │            │
│ Conteúdo        │              │ Conteúdo     │            │ Conteúdo   │
│ Principal       │              │ Responsivo   │            │ Stack      │
│                 │              │              │            │ Vertical   │
│  [Botões]       │              │ [Botões]     │            │ [Botões]   │
│                 │              │              │            │            │
├─────────────────┤              ├──────────────┤            ├────────────┤
│    FOOTER       │              │   FOOTER     │            │  FOOTER    │
└─────────────────┘              └──────────────┘            └────────────┘
```

---

## 🔐 Fluxo de Segurança

```
REQUEST → VALIDAÇÃO → AUTENTICAÇÃO → AUTORIZAÇÃO → PROCESSAMENTO
            │              │              │              │
     ┌──────┴──────┐       │              │              │
     │ Verificar   │       │              │              │
     │ Entrada     │       │              │              │
     └──────┬──────┘       │              │              │
            │    ┌─────────┴──────┐       │              │
            │    │ Usuário Logado?│       │              │
            │    └─────────┬──────┘       │              │
            │              │YES   ┌───────┴──────┐       │
            │              │      │ Proprietário?│       │
            │              │      └───────┬──────┘       │
            │              │              │YES   ┌───────┴──────┐
            │              │              │      │ Executar     │
            │              │              │      │ Operação     │
            │              │              │      └───────┬──────┘
    ERRO    │              │ ERRO         │ ERRO         │ OK
    ┌───────▼─┐      ┌──────▼──┐     ┌────▼──┐     ┌────▼─────┐
    │ Rejeitar │      │Redirecio│     │Rejeitar│     │ Retornar │
    │ 400 Bad  │      │nar Login│     │ 403    │     │ Sucesso  │
    │ Request  │      │         │     │ Forbid │     │ 200 OK   │
    └──────────┘      └─────────┘     └────────┘     └──────────┘
```

---

## 📊 Arquitetura em Camadas

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│                   (Templates HTML/CSS/JS)                   │
│  ┌──────────────┬──────────────┬──────────────────────────┐ │
│  │ agenda.html  │ agendar.html │ agendoSucesso.html       │ │
│  └──────────────┴──────────────┴──────────────────────────┘ │
└──────────────────────────┬────────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────────┐
│                  CONTROLLER LAYER                            │
│              (HomeController.java - @Controller)              │
│  ┌──────────────┬──────────────┬──────────────────────────┐  │
│  │ GET /agenda  │ GET /agendar │ POST /agendar            │  │
│  │ POST /cancel │ GET /horarios│                          │  │
│  └──────────────┴──────────────┴──────────────────────────┘  │
└──────────────────────────┬────────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────────┐
│                    SERVICE LAYER                             │
│           (AgendamentoService.java - @Service)               │
│  ┌──────────────┬──────────────┬──────────────────────────┐  │
│  │criarAgend.   │ getAgendamentos  │ getHorariosDisp.    │  │
│  │cancelarAgend │ removeAgendament │ validations...      │  │
│  └──────────────┴──────────────┴──────────────────────────┘  │
└──────────────────────────┬────────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────────┐
│                      MODEL LAYER                             │
│               (Entity Models - @Entity)                       │
│  ┌──────────────┬──────────────┬──────────────────────────┐  │
│  │Agendamento   │ Contato      │ CadastroTemporario      │  │
│  │ id           │ nome         │ codigoVerificacao       │  │
│  │ data         │ email        │ timestamp               │  │
│  │ horario      │ celular      │                         │  │
│  │ descricao    │ senha        │                         │  │
│  │ status       │              │                         │  │
│  └──────────────┴──────────────┴──────────────────────────┘  │
└──────────────────────────┬────────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────────┐
│                  DATA ACCESS LAYER                           │
│                    (In-Memory Storage)                        │
│              ArrayList<Agendamento> agendamentos              │
│              HashMap<String, String> usuariosLogados         │
└────────────────────────────────────────────────────────────────┘
```

---

## 🌐 Endpoints REST

```
GET /agenda
├─ Descrição: Lista agendamentos do usuário
├─ Método: GET
├─ Autenticação: Requerida (usuário logado)
├─ Parâmetros: Nenhum
├─ Retorno: agenda.html (Template Thymeleaf)
└─ Exemplo: http://localhost:8080/agenda

GET /agendar
├─ Descrição: Exibe formulário de novo agendamento
├─ Método: GET
├─ Autenticação: Requerida
├─ Retorno: agendar.html
└─ Exemplo: http://localhost:8080/agendar

POST /agendar
├─ Descrição: Cria novo agendamento
├─ Método: POST
├─ Autenticação: Requerida
├─ Parâmetros:
│  ├─ data: String (YYYY-MM-DD)
│  ├─ horario: String (HH:00)
│  └─ descricao: String (opcional)
├─ Validações:
│  ├─ Data > hoje
│  ├─ Horário disponível
│  └─ Campos obrigatórios
├─ Retorno: agendoSucesso.html ou agendar.html (com erro)
└─ Exemplo: POST /agendar?data=2026-04-28&horario=10:00

GET /horarios-disponiveis
├─ Descrição: Retorna horários livres (AJAX)
├─ Método: GET
├─ Parâmetros: data (YYYY-MM-DD)
├─ Retorno: Thymeleaf fragment (lista de horários)
└─ Exemplo: GET /horarios-disponiveis?data=2026-04-28

POST /cancelar-agendamento
├─ Descrição: Cancela um agendamento
├─ Método: POST
├─ Autenticação: Requerida
├─ Parâmetros: id (UUID)
├─ Validações: Apenas dono pode cancelar
├─ Retorno: Redirecionamento para /agenda
└─ Exemplo: POST /cancelar-agendamento?id=uuid-aqui
```

---

## 💾 Estrutura de Dados

### Agendamento (JSON)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "emailUsuario": "usuario@email.com",
  "nomeUsuario": "João Silva",
  "data": "2026-04-28",
  "horario": "10:00",
  "descricao": "Consulta geral",
  "status": "confirmado",
  "timestamp": 1703078400000
}
```

### Usuário Logado (Session)
```json
{
  "email": "usuario@email.com",
  "nome": "João Silva"
}
```

---

## 📈 Ciclo de Vida de um Agendamento

```
1. CRIAÇÃO
   └─ usuário preenche formulário
   └─ sistema valida dados
   └─ agendamento é criado com status "confirmado"

2. ARMAZENAMENTO
   └─ agendamento é adicionado à lista
   └─ ID único é gerado
   └─ timestamp é registrado

3. EXIBIÇÃO
   └─ usuário vê na lista de agendamentos
   └─ dados são formatados para exibição
   └─ status é mostrado visualmente

4. POSSIBILIDADES
   ├─ PERMANECER: Agendamento ativo até data/hora
   ├─ CANCELAR: Usuário clica em cancelar
   │  └─ Status muda para "cancelado"
   │  └─ Horário fica disponível novamente
   └─ EXPIRAR: Data/hora passa (teórico, sem validação)

5. PERSISTÊNCIA
   └─ dados em memória (ArrayList)
   └─ perdidos ao reiniciar aplicação
   └─ em produção: migrar para DB
```

---

## 🎯 Matriz de Funcionalidades

| Funcionalidade | Controller | Service | Model | Template | Status |
|---|---|---|---|---|---|
| Criar Agendamento | ✅ | ✅ | ✅ | ✅ | ✅ Completo |
| Listar Agendamentos | ✅ | ✅ | ✅ | ✅ | ✅ Completo |
| Cancelar Agendamento | ✅ | ✅ | ✅ | ✅ | ✅ Completo |
| Validar Data | ✅ | ✅ | - | ✅ | ✅ Completo |
| Validar Horário | ✅ | ✅ | - | ✅ | ✅ Completo |
| Impedir Double-booking | - | ✅ | - | - | ✅ Completo |
| Autenticação | ✅ | - | - | - | ✅ Completo |
| Responsividade | - | - | - | ✅ | ✅ Completo |

---

## 📋 Checklist de Verificação

### Instalação ✅
- [x] Código compilou sem erros
- [x] Dependências resolvidas
- [x] Aplicação inicia com sucesso

### Funcionalidades ✅
- [x] Visualizar agenda
- [x] Criar agendamento
- [x] Cancelar agendamento
- [x] Carregar horários dinâmicos
- [x] Validar datas

### UX/UI ✅
- [x] Design responsivo
- [x] Animações suaves
- [x] Mensagens claras
- [x] Navegação intuitiva
- [x] Cores consistentes

### Documentação ✅
- [x] Código comentado
- [x] README atualizado
- [x] Guia de uso
- [x] Documentação técnica
- [x] Changelog completo

### Testes ✅
- [x] Compilação
- [x] Execução
- [x] Rotas acessíveis
- [x] Funcionalidades básicas
- [x] Responsividade

---

**Diagrama Criado**: 25/04/2026  
**Versão**: 1.0  
**Status**: ✅ Completo

