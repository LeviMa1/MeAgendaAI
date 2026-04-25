# 🚀 DEPLOY RÁPIDO RENDER.COM

## 5 PASSOS SIMPLES

### 0️⃣ Adicione Dockerfile (Se não tiver)
Os arquivos `Dockerfile` e `.dockerignore` já devem estar na raiz do projeto.

### 1️⃣ GitHub - Suba seu código
```bash
cd C:\Users\leviv\OneDrive\Desktop\agenda\agenda
git add .
git commit -m "Ready to deploy with Docker"
git push origin main
```

### 2️⃣ Render.com - Crie conta
- Acesse: **render.com**
- Sign up with GitHub
- Autorize o acesso

### 3️⃣ Criar Web Service
- Clique: **New Web Service**
- Selecione seu repositório
- **Linguagem:** Docker (selecionará automático)
- Nome: `meagendaai`

### 4️⃣ Configurar Deploy
| Campo | Valor |
|-------|-------|
| Build Command | (deixe em branco) |
| Start Command | (deixe em branco) |
| Plan | FREE |

**Não preencha Build/Start, deixe o Docker cuidar!**

### 5️⃣ Aguarde & Acesse
- Status muda para "Live"
- Recebe URL: `https://meagendaai.onrender.com`
- Pronto! 🎉

---

## ⏱️ TEMPO ESTIMADO
- ⏳ Build Docker: 5-10 min (primeira vez - mais longo)
- ✅ Deploy: 1-2 min
- Total: ~10-15 minutos

## 🔄 Atualizações Futuras
Apenas faça push no GitHub que Render detecta e redeploya automaticamente!

```bash
git push origin main  # Render faz deploy automático
```

---

## 💡 DICAS
- Free tier é suficiente para desenvolvimento
- Docker é mais fácil que configurar Maven no Render
- Dados em memória (perdem ao reiniciar)
- Para produção, use PostgreSQL
- Reinicia automaticamente após 15 min de inatividade

Boa sorte! 🚀

