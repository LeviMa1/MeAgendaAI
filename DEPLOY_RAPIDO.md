# 🚀 DEPLOY RÁPIDO RENDER.COM

## 5 PASSOS SIMPLES

### 1️⃣ GitHub - Suba seu código
```bash
git add .
git commit -m "Ready to deploy"
git push origin main
```

### 2️⃣ Render.com - Crie conta
- Acesse: **render.com**
- Sign up with GitHub
- Autorize o acesso

### 3️⃣ Criar Web Service
- Clique: **New Web Service**
- Selecione seu repositório
- Nome: `meagendaai`

### 4️⃣ Configurar Build
| Campo | Valor |
|-------|-------|
| Build | `./mvnw clean package -DskipTests` |
| Start | `java -jar target/agenda-0.0.1-SNAPSHOT.jar` |
| Plan | FREE |

### 5️⃣ Aguarde & Acesse
- Status muda para "Live"
- Recebe URL: `https://meagendaai.onrender.com`
- Pronto! 🎉

---

## ⏱️ TEMPO ESTIMADO
- ⏳ Build: 2-5 min (primeira vez)
- ✅ Deploy: 1-2 min
- Total: ~5-7 minutos

## 🔄 Atualizações Futuras
Apenas faça push no GitHub que Render detecta e redeploya automaticamente!

```bash
git push origin main  # Render faz deploy automático
```

---

## 💡 DICAS
- Free tier é suficiente para desenvolvimento
- Dados em memória (perdem ao reiniciar)
- Para produção, use PostgreSQL
- Reinicia automaticamente após 15 min de inatividade

Boa sorte! 🚀

