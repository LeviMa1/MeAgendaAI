# 🔐 CONFIGURAR VONAGE NO RENDER.COM

## 📋 Passo 1: Obter Credenciais Vonage

1. **Acesse:** vonage.com (antigo Nexmo)
2. **Crie uma conta** ou faça login
3. **Vá para "Account" → "API Credentials"**
4. **Copie:**
   - `API Key` (chave de API)
   - `API Secret` (chave secreta)

⚠️ **IMPORTANTE:** Guarde estas credenciais em local seguro!

---

## 🌐 Passo 2: Adicionar Variáveis no Render.com

### **Via Dashboard Render:**

1. **Acesse seu serviço no Render:** render.com/dashboard
2. **Selecione sua aplicação:** meagendaai
3. **Clique em:** Environment
4. **Clique em:** "Add Environment Variable"

### **Adicione DUAS variáveis:**

#### **Variável 1:**
- **Key:** `VONAGE_API_KEY`
- **Value:** Sua API Key do Vonage (cole aqui)
- Clique **Add**

#### **Variável 2:**
- **Key:** `VONAGE_API_SECRET`
- **Value:** Seu API Secret do Vonage (cole aqui)
- Clique **Add**

#### **Variável 3 (Opcional):**
- **Key:** `VONAGE_FROM_NUMBER`
- **Value:** `MeAgenda` (ou seu nome)
- Clique **Add**

---

## 🔄 Passo 3: Fazer Deploy

1. **Após adicionar as variáveis**
2. **Vá para "Build & Deploy"**
3. **Clique em "Manual Deploy"** (canto superior direito)
4. **Ou** aguarde o Render detectar automaticamente

---

## ✅ Passo 4: Verificar se Funciona

### **Teste o SMS:**

1. **Acesse sua aplicação:** https://meagendaai.onrender.com
2. **Faça um cadastro** com um número de celular válido
3. **Você deve receber um SMS com o código** no seu celular
4. **Digite o código para confirmar**

### **Se não receber o SMS:**

1. **Verifique os Logs no Render:**
   - Dashboard → Logs
   - Procure por "Tentando enviar SMS"
   - Se disser "API Key configurada: false" → credenciais não foram adicionadas

2. **Possíveis problemas:**
   - ❌ Variáveis não foram salvas (clique **Add** corretamente)
   - ❌ Credenciais erradas (copie novamente do Vonage)
   - ❌ Falta de créditos na conta Vonage
   - ❌ Número de telefone em formato errado

---

## 🧪 Teste Local (Opcional)

Se quiser testar localmente antes de fazer deploy:

### **1. Configure em `application-dev.properties`:**
```properties
vonage.api-key=SUA_API_KEY_AQUI
vonage.api-secret=SUA_API_SECRET_AQUI
vonage.from-number=MeAgenda
```

### **2. Execute com Spring Boot:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### **3. Teste o cadastro**
- Você receberá o SMS no seu celular

---

## 📊 Fluxo Completo:

```
1. Cliente acessa: https://meagendaai.onrender.com
2. Clica em "Cadastre-se"
3. Preenche nome, email, senha
4. Clica "Cadastrar"
5. Sistema pede o celular
6. Cliente digita o celular
7. Clica "Enviar Código"
8. ✅ SMS é enviado via Vonage
9. Cliente recebe código no celular
10. Digita o código na tela
11. Código é validado
12. Cadastro finalizado com sucesso!
```

---

## 💰 Custos Vonage

- Vonage oferece **créditos grátis iniciais** para novos usuários
- SMS custa aproximadamente **0,05-0,10 USD por mensagem**
- Suficiente para testes e desenvolvimento

---

## 🆘 Troubleshooting

### **SMS não é enviado:**
```
Logger mostra: "API Key configurada: false"
→ Solução: Adicionar variáveis no Render
```

### **SMS enviado mas código errado:**
```
→ Verificar logs do Vonage
→ Checar credenciais
```

### **Erro 401 ou 403:**
```
→ API Key ou Secret está errado
→ Copiar novamente do Vonage
```

---

## ✨ PRONTO!

Seu sistema MeAgendaAI agora envia SMS reais para os usuários! 🎉

Qualquer dúvida, verifique os logs no Render.

