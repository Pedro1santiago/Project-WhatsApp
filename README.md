
# 📱 Project WhatsApp - Clone em Android Kotlin

Este é um clone funcional do WhatsApp, desenvolvido como projeto de estudo para portfólio. Ele simula um mensageiro com funcionalidades básicas como login, cadastro, envio de mensagens e imagens, tudo utilizando Firebase.

---

## 🔧 Tecnologias Utilizadas

- Kotlin
- Android Studio
- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage
- RecyclerView
- XML Layouts

---

## 📸 Funcionalidades

- Cadastro e autenticação de usuários
- Lista de usuários cadastrados
- Envio e recebimento de mensagens em tempo real
- Envio de imagens pelo chat
- Interface inspirada no WhatsApp

---

## 🚀 Como Rodar o Projeto

Siga os passos abaixo para rodar o app localmente no Android Studio:

1. **Clone este repositório:**

```bash
git clone https://github.com/Pedro1santiago/Project-WhatsApp.git
```

2. **Abra o projeto no Android Studio**

3. **Configure o Firebase:**
    - Acesse [https://console.firebase.google.com](https://console.firebase.google.com)
    - Crie um novo projeto (ou use um existente)
    - Clique em **Adicionar App > Android**
    - Use o seguinte `applicationId`: `com.pedro.whatsapp`
    - Baixe o arquivo `google-services.json`
    - Coloque esse arquivo dentro da pasta `app/` do projeto

4. **Ative os seguintes serviços do Firebase:**
    - Autenticação por e-mail/senha (Authentication)
    - Firebase Realtime Database
    - Firebase Storage

5. **No Android Studio:**
    - Vá em `Tools > Firebase`
    - Use o assistente do Firebase para vincular os serviços (Auth, Database e Storage)
    - Clique em **"Sync Now"** se aparecer aviso de sincronização do Gradle

6. **Execute o app:**
    - Conecte um dispositivo físico ou use um emulador
    - Clique no botão **▶️ Run** no Android Studio

---

> ⚠️ O Firebase é essencial para o app funcionar corretamente.  
> Sem configurar `google-services.json`, login e chat não irão funcionar.

---

## 👨‍💻 Autor

Desenvolvido por **Pedro Santiago**  
🔗 [GitHub](https://github.com/Pedro1santiago)

---


