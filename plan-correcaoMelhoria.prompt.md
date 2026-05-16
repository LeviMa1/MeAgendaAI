# Plan: Correção e Melhoria Completa do Projeto

Análise identificou **bugs críticos**, **falhas de arquitetura** e **problemas de UX** que precisam ser corrigidos. O foco é estabilizar o sistema, corrigir erros ativos e melhorar a confiabilidade sem banco de dados externo.

---

## Bugs Críticos a Corrigir

**1. Corrigir expressão SpEL nula em `agenda.html` (linha ~307)**
- Substituir `"agendamento.descricao and !agendamento.descricao.isEmpty()"` por `${agendamento.descricao != null and agendamento.descricao != ''}`

**2. Corrigir expressão nula em `login.html` (linha ~182)**
- Substituir `${!sucesso}` por `${sucesso != null and !sucesso}` para evitar erro quando `sucesso` é `null` no GET

**3. Corrigir login de admin bloqueado pelo HTML5 no `login.html`**
- O `<input type="email">` do navegador exige `@`, impedindo "admin" de ser digitado. Trocar para `type="text"` ou usar validação customizada

**4. Corrigir sessão multi-usuário em `HomeController.java`**
- `usuariosLogados` é um `static Map` com chaves fixas `"email"/"nome"`, sobrescrito por qualquer login. Usar `HttpSession` do Spring para isolar sessão por navegador

**5. Corrigir sessão do admin em `AdminService.java`**
- `adminLogado` é `static`, fazendo `estaLogado()` retornar `true` globalmente. Usar `HttpSession` também para o admin

**6. Adicionar rota de logout do usuário comum em `HomeController.java`**
- Não existe `/logout` para usuários normais; apenas `/admin/logout` existe

**7. Corrigir layout mobile em `admin.html`**
- Botão "Ver Contatos" deslocando "Sair" no mobile; ajustar `flex-wrap` e ordem do CSS responsivo

---

## Melhorias de Arquitetura

**8. Substituir dados em memória por H2 (banco embutido) ou arquivo JSON persistido**
- Todos os dados (`contatos`, `agendamentos`, `diasDisponiveis`) são perdidos ao reiniciar. Adicionar H2 em-arquivo no `pom.xml` e `application.properties` para persistência sem infraestrutura externa

**9. Criptografar senhas em `HomeController.java` e `Contato.java`**
- Senhas armazenadas e comparadas em texto puro. Usar `BCryptPasswordEncoder` (já disponível no Spring Security)

**10. Corrigir `AgendamentoService.getHorariosDisponiveis()`**
- O método gera horários fixos (8–17h) ignorando o que o admin configurou; deve consultar `AdminService.getHorariosPorData()`

**11. Corrigir encoding do email na URL de redirect em `HomeController.java`**
- `return "redirect:/pedir-celular?email=" + email` pode quebrar com emails com `+` ou outros caracteres especiais. Usar `URLEncoder.encode()`

---

## Considerações Adicionais

1. **SMS/Verificação**: O envio via Vonage ainda imprime no console (modo teste). Deseja manter assim por enquanto, ativar o Vonage real com as variáveis de ambiente, ou explorar alternativa gratuita como **CallMeBot** (WhatsApp gratuito via API simples)?
R: Testar então o CallMeBot, contanto que a solução seja melhor ou semelhante.
2. **Banco de dados**: H2 em modo arquivo é a solução mais simples — sem custo, sem servidor externo, dados persistem entre reinicializações. Quer seguir por esse caminho ou prefere manter em memória e aceitar a perda ao reiniciar?
R: Pode realizar a implementação do H2.
3. **Deploy no Render**: Com H2 em arquivo, os dados ainda seriam perdidos em cada deploy/restart no Render (disco efêmero). Para persistência real em produção, seria necessário migrar para PostgreSQL (disponível gratuitamente no Render). Incluir esse passo no plano?
R: Se for a melhor opção, pode ser realizado. Caso exista a possibilidade de migrar para MySQL, opto por ela.

