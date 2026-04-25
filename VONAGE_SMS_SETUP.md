# Configuração de SMS Vonage (100% Gratuito)

## 📱 Como Configurar Vonage para Enviar SMS Grátis

### ✨ Vantagens
- ✅ Créditos grátis no cadastro (~5€-10€)
- ✅ SMS muito barato depois (~USD 0.04)
- ✅ Funciona em qualquer país
- ✅ Sem cartão de crédito obrigatório
- ✅ Bom para testes e produção

### 1. Criar Conta no Vonage

1. Acesse: https://www.vonage.com/communications-apis/sms/
2. Clique em **"Start Building"** ou **"Sign up"**
3. Preencha os dados:
   - Email
   - Nome completo
   - País
   - Senha forte
4. Confirme seu email
5. Você receberá créditos grátis automaticamente!

### 2. Obter Credenciais API

1. Faça login no Console Vonage
2. Vá para **Applications** no menu lateral
3. Clique em **"Create an application"**
4. Preencha:
   - Application name: "MeAgenda"
   - SMS capabilities: Ativar
5. Clique em **"Create"**
6. Na página da aplicação, copie:
   - **API Key**
   - **API Secret**

### 3. Configurar Número Remetente

#### Opção A: Usar nome alfanumérico (Recomendado)
- Nome será exibido como remetente (ex: "MeAgenda")
- Nenhuma configuração adicional

#### Opção B: Usar número Vonage
1. No Console Vonage
2. Vá para **Numbers**
3. **Buy Virtual Numbers** se necessário
4. Copie o número

### 4. Configurar Variáveis de Ambiente

#### Windows PowerShell
```bash
$env:VONAGE_API_KEY = "seu-api-key-aqui"
$env:VONAGE_API_SECRET = "seu-api-secret-aqui"
$env:VONAGE_FROM_NUMBER = "MeAgenda"
```

#### Linux/Mac
```bash
export VONAGE_API_KEY="seu-api-key-aqui"
export VONAGE_API_SECRET="seu-api-secret-aqui"
export VONAGE_FROM_NUMBER="MeAgenda"
```

#### Opção 2: No arquivo application.properties
```properties
vonage.api-key=seu-api-key-aqui
vonage.api-secret=seu-api-secret-aqui
vonage.from-number=MeAgenda
```

### 5. Testando

#### Modo Teste (Sem Credenciais)
Se você não configurar as variáveis:
- O sistema roda em modo teste
- Os códigos aparecem no console
- Perfeito para desenvolvimento

#### Modo Real (Com Credenciais)
1. Configure as variáveis de ambiente
2. Acesse: `http://localhost:8080/cadastro`
3. Preencha o formulário
4. Informa seu celular
5. **SMS é recebido em segundos!**
6. Digite o código para confirmar

## 🧪 Como Testar Gratuitamente

1. Cadastre-se em Vonage
2. Você receberá 5€-10€ em créditos
3. Configure as credenciais
4. Cada SMS custa ~USD 0.04
5. Você pode enviar ~125-250 SMSs grátis!

## 📞 Formato do SMS Recebido

```
🔐 Seu código de verificação é: 1234
⏱️ Validade: 10 minutos
⚠️ Não compartilhe este código com ninguém.
```

## ❓ Troubleshooting

### "Erro ao enviar SMS"
1. Verifique se API Key está correto
2. Verifique se API Secret está correto
3. Verifique se sua conta tem saldo de créditos
4. Verifique se o número é válido (11 dígitos para Brasil)

### "Credenciais não configuradas"
- O sistema entra em modo teste
- Verifique as variáveis de ambiente
- Rode: `echo $env:VONAGE_API_KEY` (PowerShell)

### SMS não chega
1. Aguarde alguns segundos (às vezes demora)
2. Verifique se o número está correto
3. Verifique se o SMS não foi bloqueado como spam
4. Tente com outro número

### "Invalid credentials"
1. Copie novamente do Console Vonage
2. Certifique-se de que não há espaços
3. Tente fazer logout e login novamente

## 💰 Custos

- **Cadastro:** Gratuito
- **Créditos iniciais:** 5€-10€ grátis
- **SMS depois:** ~USD 0.04 por mensagem
- **Mensagem recebida:** Gratuita (não cobra por receber)

## 🔒 Segurança

- ✅ Nunca comite credenciais no Git
- ✅ Use variáveis de ambiente
- ✅ Revogue credenciais se comprometidas
- ✅ Código expira em 10 minutos
- ✅ Código é de 4 dígitos aleatórios

## 📊 Dashboard Vonage

No Console você pode ver:
- ✅ Saldo de créditos
- ✅ SMSs enviados
- ✅ Taxa de entrega
- ✅ Histórico de mensagens

## 🌍 Suporta Múltiplos Países

- 🇧🇷 Brasil
- 🇵🇹 Portugal
- 🇪🇸 Espanha
- 🇬🇧 Reino Unido
- 🇺🇸 Estados Unidos
- E muitos mais!

## 📈 Próximos Passos

Após configurar Vonage:
- ✅ SMS é enviado em tempo real
- ✅ Usuários confirmam código
- ✅ Cadastro é concluído
- ✅ Sistema está pronto para produção

## 🚀 Escalando para Produção

Quando precisar enviar mais SMS:
1. Sua conta pode ter limite diário
2. Aumente saldo conforme necessário
3. SMS fica ainda mais barato em volumes grandes
4. Vonage é confiável para produção

## 💡 Dica

Se quiser SMS mais barato em longo prazo:
- Pesquise por provedores SMS locais do seu país
- Muitos têm taxas mais baixas
- Vonage é ótimo para começar

## 📞 Suporte

- Documentação oficial: https://developer.vonage.com/sms/code-snippets/send-an-sms/
- Status: https://status.vonage.com/
- Support: https://support.vonage.com/

