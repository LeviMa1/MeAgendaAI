# 🚀 GUIA COMPLETO: HOSPEDAGEM NO RENDER.COM

## ✅ PASSO 1: Preparar o Repositório GitHub

1. **Crie uma conta no GitHub** (se não tiver): github.com

2. **Crie um novo repositório público** com o nome `MeAgendaAI` ou similar

3. **No seu computador, entre na pasta do projeto:**
   ```bash
   cd C:\Users\leviv\OneDrive\Desktop\agenda\agenda
   ```

4. **Inicialize o git (se não estiver já inicializado):**
   ```bash
   git init
   git add .
   git commit -m "Initial commit - MeAgendaAI"
   ```

5. **Conecte ao repositório GitHub:**
   ```bash
   git branch -M main
   git remote add origin https://github.com/SEU_USUARIO/MeAgendaAI.git
   git push -u origin main
   ```

---

## ✅ PASSO 0.5: Arquivos Necessários (IMPORTANTE!)

Certifique-se de que você tem estes arquivos na raiz do projeto:
- ✅ `Dockerfile` - Define como compilar e rodar a aplicação
- ✅ `.dockerignore` - Lista arquivos para não incluir no build
- ✅ `pom.xml` - Arquivo Maven (já existe no seu projeto)

**Se não tiver, adicione agora antes de fazer push!**

---

## 🔑 PASSO 2: Criar Conta no Render.com

1. **Acesse:** render.com
2. **Clique em "Sign Up"**
3. **Escolha "Sign up with GitHub"**
4. **Autorize o Render a acessar seus repositórios**
5. **Finalize o cadastro**

---

## 🛠️ PASSO 3: Criar o Web Service

1. **No dashboard do Render, clique:** "New Web Service"
2. **Selecione:** "Build and deploy from a Git repository"
3. **Conecte seu repositório:** Escolha `MeAgendaAI` (ou o nome que criou)
4. **Preencha as informações:**

   | Campo | Valor |
   |-------|-------|
   | **Name** | meagendaai (sem espaços) |
   | **Environment** | Docker (selecionará automático ao detectar Dockerfile) |
   | **Region** | Ohio (US) ou São Paulo (BR) |
   | **Branch** | main |
   | **Build Command** | (deixe em branco - Docker cuida) |
   | **Start Command** | (deixe em branco - Dockerfile cuida) |

5. **Plano:** Selecione **Free** (na parte inferior)

⚠️ **IMPORTANTE:** 
   - O Render detectará automaticamente o **Dockerfile** no repositório
   - Não precisa adicionar Build Command ou Start Command
   - Deixe os campos em branco para que use o Dockerfile

---

## 🔐 PASSO 4: Adicionar Variáveis de Ambiente

1. **Desça até "Environment"**
2. **Clique em "Add Environment Variable"**
3. **Adicione as seguintes variáveis:**

   ```
   PORT = 8080
   JAVA_TOOL_OPTIONS = -Xmx512m
   ```

4. **Clique em "Create Web Service"**

---

## ⏳ PASSO 5: Acompanhar o Deploy

1. **Você será redirecionado para a página do seu serviço**
2. **Clique em "Logs"** para ver o progresso
3. **Espere até ver:** "Build successful!"
4. **Depois:** "Deploying..."
5. **Finalmente:** "Live"

---

## 🌐 PASSO 6: Acessar Sua Aplicação

1. **Quando o status estiver "Live", você receberá uma URL**
2. **Será algo como:** `https://meagendaai.onrender.com`
3. **Clique e pronto! Sua app está online!**

---

## 🔄 ATUALIZAÇÕES FUTURAS

A cada vez que você fizer um push no GitHub:
```bash
git add .
git commit -m "Descrição da mudança"
git push origin main
```

O Render detectará automaticamente e fará o deploy!

---

## ⚠️ NOTAS IMPORTANTES

1. **Plano Gratuito Render:**
   - Spin down automático após 15 min de inatividade
   - Pode levar 30 seg para "acordar" da primeira requisição
   - Perda de dados em memória ao reiniciar
   - Suficiente para desenvolvimento e testes

2. **Seu banco de dados:**
   - Atualmente está em memória (H2)
   - Dados serão perdidos ao reiniciar
   - Para produção, considere PostgreSQL no Render

3. **Dados persistem com:**
   - PostgreSQL do Render
   - Arquivos em /tmp (limitados)

---

## 📊 PROGRESSO DE DEPLOY

- ⏳ **0-2 min:** Build Java (pode demorar)
- ✅ **Quando ver "Live":** Está pronto!
- 🔗 **URL aparecerá no topo da página**

---

## 🆘 TROUBLESHOOTING

**Erro: "Build failed"**
- Verifique se o `pom.xml` está correto
- Certifique-se de ter todos os arquivos necessários

**Erro: "Port already in use"**
- Já foi resolvido (Render detecta automático)

**Erro: "Cannot find JAR file"**
- Certifique-se de que `mvnw` tem permissão de execução

---

## ✨ PRONTO!

Seu sistema MeAgendaAI está online e acessível de qualquer lugar do mundo! 🎉

Compartilhe a URL com seus clientes para que eles agendem seus compromissos!

